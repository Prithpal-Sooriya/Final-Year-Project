package main

/*
IPFS basics in GO:

first we need to construct an IPFS core node
then spin up a http api server to operate on this node.
  - in effect, we are creating our own IPFS daemon.
  - useful for testing various things, or building own application in go, that wants to embed an ipfs daemon.
 */


 //imports
 import(
   "context" //package that defines context type (info to server)
   "log" //allows logging
   "net" //networking package

   //ipfs packages
   commands "github.com/ipfs/go-ipfs/commands"
   core "github.com/ipfs/go-ipfs/core"
   corehttp "github.com/ipfs/go-ipfs/core/corehttp"
 )

//main method
func main() {

  //https://golang.org/pkg/context/#WithCancel
  // (ctx Context, cancel CancelFunc)
  //...returns a copy of parent with new Done (closed) channel
  ctx, cancel := context.WithCancel(context.Background())
  defer cancel() //wait on the cancel function

  //core part of ipfs-go
  nd, err := core.NewNode(ctx, &core.BuildCfg{})
  if(err != nil) {
    log.Fatal(err)
  }

  //commands part of ipfs-go
  cctx := commands.Context {
    Online: true,
    ConstructNode: func() (*core.IpfsNode, error) {
      return nd, nil //returning new node created from core
    },
  }

  //listen
  list, err := net.Listen("tcp", ":0")
  if(err != nil) {
    log.Fatal(err)
  }

  //message for listening
  log.Println("Listening on: ", list.Addr())

  //serve function
  if err := corehttp.Serve(nd, list, corehttp.CommandOption(cctx)); err != nil {
    log.Fatal(err)
  }

}
