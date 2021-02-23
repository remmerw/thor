package thor

import (
	"context"
	dht "github.com/libp2p/go-libp2p-kad-dht"
	"sync"
	"time"

	"github.com/libp2p/go-libp2p-core/peer"
	"github.com/libp2p/go-libp2p-core/routing"
)

type ResolveInfo interface {
	Resolved(Data []byte)
	Close() bool
}

func (n *Node) DecodeName(name string) string {

	pid, err := peer.Decode(name)
	if err != nil {
		n.Listener.Error("Error decoding name")
		return ""
	}
	return pid.Pretty()
}

func (n *Node) ResolveName(info ResolveInfo, name string, offline bool, dhtRecords int) error {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	go func(stream ResolveInfo) {
		for {
			if ctx.Err() != nil {
				break
			}
			if stream.Close() {
				cancel()
				break
			}
			time.Sleep(time.Duration(n.Responsive) * time.Millisecond)
		}
	}(info)

	pid, err := peer.Decode(name)
	if err != nil {
		cancel()
		return err
	}

	// Use the routing system to get the name.
	// Note that the DHT will call the ipns validator when retrieving
	// the value, which in turn verifies the ipns record signature
	ipnsKey := "/ipns/" + string(pid)

	var opts []routing.Option
	opts = append(opts, dht.Quorum(dhtRecords))
	if offline {
		opts = append(opts, routing.Offline)
	}

	vals, err := n.Routing.SearchValue(ctx, ipnsKey, opts...)

	if err != nil {
		cancel()
		return err
	}
	wg := &sync.WaitGroup{}
	for val := range vals {
		info.Resolved(val)
	}
	wg.Wait()

	return nil
}
