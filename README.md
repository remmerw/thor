# thor


go get -d github.com/remmerw/thor

cd $GOPATH/src/github.com/remmerw/thor

go mod vendor

go mod tidy

cd $HOME

set GO111MODULE=off

gomobile bind -o thor-1.1.3.aar -v -androidapi=26 -target=android -ldflags="-s -w" github.com/remmerw/thor
