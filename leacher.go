package thor

import (
	"context"
	blocks "github.com/ipfs/go-block-format"
	cid "github.com/ipfs/go-cid"
	bstore "github.com/ipfs/go-ipfs-blockstore"
	dshelp "github.com/ipfs/go-ipfs-ds-help"
	"github.com/ipfs/go-verifcid"
)

var _ bstore.Blockstore = (*Leacher)(nil)

// NewBlockstore returns a default Blockstore implementation
// using the provided datastore.Batching backend.
func NewLeacher(listener Listener) bstore.Blockstore {
	return &Leacher{
		Listener: listener,
	}
}

type Leacher struct {
	Listener Listener
	rehash   bool
}

func (bs *Leacher) HashOnRead(enabled bool) {
	bs.rehash = enabled
}

func (bs *Leacher) Get(k cid.Cid) (blocks.Block, error) {
	return nil, bstore.ErrNotFound
}

func (bs *Leacher) Put(block blocks.Block) error {
	if err := verifcid.ValidateCid(block.Cid()); err != nil {
		return err
	}
	k := dshelp.CidToDsKey(block.Cid())

	// Has is cheaper than Put, so see if we already have it
	exists := bs.Listener.BlockHas(k.String())
	if exists {
		return nil // already stored.
	}
	bs.Listener.BlockPut(k.String(), block.RawData())
	return nil
}

func (bs *Leacher) PutMany(blocks []blocks.Block) error {

	for _, b := range blocks {
		err := bs.Put(b)
		if err != nil {
			return err
		}
	}
	return nil
}

func (bs *Leacher) Has(k cid.Cid) (bool, error) {
	return false, nil
}

func (bs *Leacher) GetSize(k cid.Cid) (int, error) {
	return -1, bstore.ErrNotFound
}

func (bs *Leacher) DeleteBlock(k cid.Cid) error {
	return nil
}

// AllKeysChan runs a query for keys from the blockstore.
// this is very simplistic, in the future, take dsq.Query as a param?
//
// AllKeysChan respects context.
func (bs *Leacher) AllKeysChan(ctx context.Context) (<-chan cid.Cid, error) {
	return nil, nil
}
