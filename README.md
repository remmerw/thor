# thor


go get -d github.com/remmerw/thor


cd $GOPATH/src/github.com/remmerw/thor
go mod vendor
go mod tidy

cd $HOME
set GO111MODULE=off

gomobile bind -o thor.aar -v -androidapi=26 -target=android github.com/remmers/thor
