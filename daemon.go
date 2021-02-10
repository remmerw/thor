package thor

import (
	"context"
	_ "expvar"
	"fmt"
	"github.com/ipfs/go-bitswap"
	bsnet "github.com/ipfs/go-bitswap/network"
	"github.com/ipfs/go-blockservice"

	blockstore "github.com/ipfs/go-ipfs-blockstore"
	"github.com/ipfs/go-ipns"
	"github.com/ipfs/go-merkledag"
	"github.com/libp2p/go-libp2p"
	connmgr "github.com/libp2p/go-libp2p-connmgr"
	"github.com/libp2p/go-libp2p-core/crypto"
	"github.com/libp2p/go-libp2p-core/host"
	"github.com/libp2p/go-libp2p-core/peer"
	"github.com/libp2p/go-libp2p-core/peerstore"
	"github.com/libp2p/go-libp2p-core/routing"
	dht "github.com/libp2p/go-libp2p-kad-dht"
	noise "github.com/libp2p/go-libp2p-noise"
	"github.com/libp2p/go-libp2p-peerstore/pstoremem"
	libp2pquic "github.com/libp2p/go-libp2p-quic-transport"
	record "github.com/libp2p/go-libp2p-record"
	tls "github.com/libp2p/go-libp2p-tls"

	ma "github.com/multiformats/go-multiaddr"
	_ "net/http/pprof"

	"time"
)

func daemon(n *Node, ctx context.Context) error {

	// let the user know we're going.
	n.Listener.Info("Initializing daemon...")

	var Swarm []string

	Swarm = append(Swarm, fmt.Sprintf("/ip4/0.0.0.0/tcp/%d", n.Port))
	Swarm = append(Swarm, fmt.Sprintf("/ip6/::/tcp/%d", n.Port))
	Swarm = append(Swarm, fmt.Sprintf("/ip4/0.0.0.0/udp/%d/quic", n.Port))
	Swarm = append(Swarm, fmt.Sprintf("/ip6/::/udp/%d/quic", n.Port))

	var err error
	mAddresses, err := listenAddresses(Swarm)
	if err != nil {
		return err
	}

	id, err := peer.Decode(n.PeerID)
	if err != nil {
		return fmt.Errorf("invalid peer id")
	}

	sk, err := DecodePrivateKey(n.PrivateKey)
	if err != nil {
		return err
	}

	n.PeerStore = pstoremem.NewPeerstore()
	n.RecordValidator = record.NamespacedValidator{
		"pk":   record.PublicKeyValidator{},
		"ipns": ipns.Validator{KeyBook: n.PeerStore},
	}

	err = pstoreAddSelfKeys(id, sk, n.PeerStore)
	if err != nil {
		return err
	}

	n.BlockStore = blockstore.NewBlockstore(n.DataStore)

	grace, err := time.ParseDuration(n.GracePeriod)
	if err != nil {
		return fmt.Errorf("parsing Swarm.ConnMgr.GracePeriod: %s", err)
	}
	n.ConnectionManager = connmgr.NewConnManager(n.LowWater, n.HighWater, grace)

	// HOST and Routing
	var opts []libp2p.Option
	opts = append(opts, libp2p.ListenAddrs(mAddresses...))
	opts = append(opts, libp2p.UserAgent(n.Agent))
	opts = append(opts, libp2p.ChainOptions(libp2p.Security(tls.ID, tls.New), libp2p.Security(noise.ID, noise.New)))
	opts = append(opts, libp2p.ConnectionManager(n.ConnectionManager))
	opts = append(opts, libp2p.Transport(libp2pquic.NewTransport))
	opts = append(opts, libp2p.DefaultTransports)
	opts = append(opts, libp2p.Ping(false))
	opts = append(opts, libp2p.ChainOptions(libp2p.EnableAutoRelay(), libp2p.DefaultStaticRelays()))

	// Let this host use the DHT to find other hosts
	opts = append(opts, libp2p.Routing(func(host host.Host) (routing.PeerRouting, error) {

		n.Routing, err = dht.New(
			ctx, host, dht.Concurrency(n.Concurrency),
			dht.Mode(dht.ModeClient),
			dht.DisableAutoRefresh(),
			dht.Validator(n.RecordValidator))

		bitSwapNetwork := bsnet.NewFromIpfsHost(host, n.Routing)

		exchange := bitswap.New(ctx, bitSwapNetwork, n.BlockStore,
			bitswap.ProvideEnabled(false))

		n.BlockService = blockservice.New(n.BlockStore, exchange)
		n.DagService = merkledag.NewDAGService(n.BlockService)
		return n.Routing, err
	}))

	n.Host, err = constructPeerHost(ctx, id, n.PeerStore, opts)
	if err != nil {
		return fmt.Errorf("constructPeerHost: %s", err)
	}

	n.Listener.Info("Daemon is ready")

	n.Running = true

	for {
		n.Listener.Verbose("Daemon still running ...")
		time.Sleep(10 * time.Second)
	}

	return nil
}

func (n *Node) PidCheck(pid string) error {

	var err error
	_, err = peer.Decode(pid)
	if err != nil {
		return fmt.Errorf("invalid peer id")
	}
	return nil
}

func listenAddresses(addresses []string) ([]ma.Multiaddr, error) {
	var listen []ma.Multiaddr
	for _, addr := range addresses {
		maddr, err := ma.NewMultiaddr(addr)
		if err != nil {
			return nil, fmt.Errorf("failure to parse config.Addresses.Swarm: %s", addresses)
		}
		listen = append(listen, maddr)
	}

	return listen, nil
}

func pstoreAddSelfKeys(id peer.ID, sk crypto.PrivKey, ps peerstore.Peerstore) error {
	if err := ps.AddPubKey(id, sk.GetPublic()); err != nil {
		return err
	}

	return ps.AddPrivKey(id, sk)
}

func constructPeerHost(ctx context.Context, id peer.ID, ps peerstore.Peerstore, options []libp2p.Option) (host.Host, error) {
	pkey := ps.PrivKey(id)
	if pkey == nil {
		return nil, fmt.Errorf("missing private key for node ID: %s", id.Pretty())
	}
	options = append([]libp2p.Option{libp2p.Identity(pkey), libp2p.Peerstore(ps)}, options...)

	return libp2p.New(ctx, options...)
}
