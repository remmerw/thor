package thor

import (
	"context"
	dag "github.com/ipfs/go-merkledag"
	uio "github.com/ipfs/go-unixfs/io"
	"github.com/ipfs/interface-go-ipfs-core/path"
	"time"

	"io"
)

type Loader struct {
	DagReader  uio.DagReader
	Size       int64
	Data       []byte
	Read       int
	Responsive int
}

type LoaderClose interface {
	Close() bool
}

type WriterStream interface {
	Load(int) (int, error)
}

func (n *Node) GetLoader(paths string, close LoaderClose) (*Loader, error) {
	ctx, cancel := context.WithCancel(context.Background())

	var err error
	var done = false

	go func(stream LoaderClose) {
		for {
			if ctx.Err() != nil {
				break
			}
			if done {
				break
			}
			if stream.Close() {
				cancel()
				break
			}
			time.Sleep(time.Duration(n.Responsive) * time.Millisecond)
		}
	}(close)

	p := path.New(paths)

	dagService := dag.NewReadOnlyDagService(dag.NewSession(ctx, n.DagService))

	nd, err := n.ResolveNode(ctx, dagService, p)
	if err != nil {
		return nil, err
	}

	dr, err := uio.NewDagReader(ctx, nd, dagService)
	if err != nil {
		return nil, err
	}

	size := dr.Size()
	done = true
	return &Loader{DagReader: dr, Size: int64(size), Responsive: n.Responsive}, nil

}

func (fd *Loader) Seek(position int64, close LoaderClose) error {
	var done = false
	go func(stream LoaderClose) {
		for {
			if done {
				break
			}
			if stream.Close() {
				fd.Close()
				break
			}
			time.Sleep(time.Duration(fd.Responsive) * time.Microsecond)
		}
	}(close)
	_, err := fd.DagReader.Seek(position, 0)
	done = true
	if err != nil {
		return err
	}

	return nil
}

func (fd *Loader) Close() error {
	return fd.DagReader.Close()
}

func (fd *Loader) Load(size int64, close LoaderClose) error {

	var done = false
	go func(stream LoaderClose) {
		for {
			if done {
				break
			}
			if stream.Close() {
				fd.Close()
				break
			}
			time.Sleep(time.Duration(fd.Responsive) * time.Microsecond)
		}
	}(close)

	buf := make([]byte, size)
	fd.Read = 0

	n, err := fd.DagReader.Read(buf)
	done = true
	if n != 0 {
		fd.Read = n
		fd.Data = buf[:n]
	}
	if err != nil {
		if err == io.EOF {
			return nil
		}
		return err
	}
	return nil
}
