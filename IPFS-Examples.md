# Examples from ipfs.io
Used to hopefully get a better understanding of IPFS

## blocks
  - large files will be broken down into a merkel DAG
  - you can use operations to see individual blocks, and even use the parent node to "get/cat" an output

## swarm
  - init a daemon to be part of a swarm
  - ipfs swarm peers -> see all open connections with peers
  - ipfs connect -> try to create an open connection with a specific peer
  - ipfs disconnect -> disconnect/close a connection with a specific peer

## pinning
  - ipfs will cache items searched (cat/get) or added (add)
    - overtime it will be garbage collected
  - pin items/objects to notify garbage collector to keep items (but overtime, if not used it will be removed)
    - added items are pinned by default.

## make own ipfs service
- ipfs has few default services run by default
  - dht, bitswap, diagnostic service

- they all:
  - register a handler on ipfs peerhost
  - listens on it for new connections

- "corenet" package has very clean interface to this functionality
  - plan = build a demo service to try this out!

service host code (deprecated tho...)

```golang
package main

//imports
import (
  //this is a format package
    "fmt" //formatted i/o with functions simmilar to C (printf, scanf, ...)

    core "github.com/ipfs/go-ipfs/core" //go ipfs
    corenet "github.com/ipfs/go-ipfs/core/corenet" //core service of ipfs
    fsrepo "github.com/ipfs/go-ipfs/repo/fsrepo"

    "code.google.com/p/go.net/context" //links to golang
)

//main method
func main() {

    // Basic ipfsnode setup --> need to set up the service host
    //default ipfsnode from the config in users (~./ipfs dir)
    r, err := fsrepo.Open("~/.ipfs") //open location of .ipfs dir (in home directory)
    if err != nil {
        panic(err) //not found, then error message
    }

    ctx, cancel := context.WithCancel(context.Background()) //get context, store into ctx and cancel
    defer cancel() //like a wait async method

    //build repo
    cfg := &core.BuildCfg{
        Repo:   r,
        Online: true,
    }

    //create new ipfs node, gets an error for failure
    nd, err := core.NewNode(ctx, cfg)

    if err != nil {
        panic(err) //error message
    }

    /* next, we are building the service */

    list, err := corenet.Listen(nd, "/app/whyrusleeping") //listen for user (i think)
    if err != nil {
        panic(err)
    }

    //if no error, then output "I am peer, <peer identity from node>"
    fmt.Printf("I am peer: %s\n", nd.Identity.Pretty())

    //infinite for loop, until return statement
    for {
        con, err := list.Accept() //list of accepted users
        if err != nil {
            fmt.Println(err)
            return //error return
        }

        defer con.Close() //defer closing connection

        //output message to peer
        fmt.Fprintln(con, "Hello! This is whyrusleepings awesome ipfs service")
        fmt.Printf("Connection from: %s\n", con.Conn().RemotePeer())
    }
}
```

client code

```golang
package main

//imports
import (
    "fmt"
    "io"
    "os"

    core "github.com/ipfs/go-ipfs/core"
    corenet "github.com/ipfs/go-ipfs/core/corenet"
    peer "github.com/ipfs/go-ipfs/p2p/peer"
    fsrepo "github.com/ipfs/go-ipfs/repo/fsrepo"

    "golang.org/x/net/context"
)

//main method
func main() {
    //check argument size given
    if len(os.Args) < 2 {
        fmt.Println("Please give a peer ID as an argument") //need to give peer id
        return
    }
    target, err := peer.IDB58Decode(os.Args[1]) //decode id, to get target id
    if err != nil {
        panic(err)
    }

    // Basic ipfsnode setup
    r, err := fsrepo.Open("~/.ipfs")
    if err != nil {
        panic(err)
    }

    ctx, cancel := context.WithCancel(context.Background())
    defer cancel()

    //configuration info about client
    cfg := &core.BuildCfg{
        Repo:   r, //from /.ipfs
        Online: true, //just to note that user is online
    }

    //create new node
    nd, err := core.NewNode(ctx, cfg)

    if err != nil {
        panic(err)
    }

    //client message to console
    fmt.Printf("I am peer %s dialing %s\n", nd.Identity, target)

    con, err := corenet.Dial(nd, target, "/app/whyrusleeping") //try to connect to server
    if err != nil {
        fmt.Println(err)
        return
    }

    //copy output from server.
    io.Copy(os.Stdout, con)
}

/*
client will set up their ipfs node (moderately expensive, normally wont spin up instance for single connection)
dail the service we just created
*/

```

run on cmd
```
ipfs init #(if not done before)
go run host.go #should print out peersID, copy it and use on a second machine

# second machine
ipfs init #(if not done before)
go run client.go <peerID>

##should print "Hello!, this is whyrusleeping awesome ipfs service"
```

Better than net package because:
- dail specific peerID, no matter what IP address is at the moment
- take advantage of NAT traversal build in this net package
- instead of port number, get much more meaningful protocol ID string.


## IPFS, distributed git
1) clone a repo, bare clone
  - where does not have a default remote origin repository
  - it can be used as a server
  - slightly different format than normal repo

2) cd into repo, and `git update-server-info`
  - or unpack all git objects
    - `cp objects/pack/*.pack .`
    - `git unpack-objects < ./*.pack`
    - `rm ./*.pack`
  - it will break large pack files into individual objects (allow IPFS to deduplicate objects, if you add multiple versions of this git repo)

3) repo is ready to serve
  - go to root dir of repo
    - `ipfs add -r .`

4) now try cloning it
  - `git clone http://localhost:8080/ipfs/<hash>`
  - `git clone http://ipfs.io/ipfs/<hash>`

Use cases!!!
- Golang uses version control packs for imports
  - allows developer to use specific version of import.
  - doesnt allow `localhost` so will need to use public http gateways: `gateway.ipfs.io/ipfs<hash>`
    - no security protection (e.g. man in the middle attack), so maybe use domain name that redirects to localhost

## static site hosting!
- `ipfs add -r` the dir of the index.html
  - the last hash (root dir hash) is needed
- open up hash on localhost daemon or http gateway (http will allow to see if other peers has hosted)

Remove hashes (to make it more easier to read)
- DNS TXT record, containing `dnslink=/ipfs/<hash>`
  - once other peers gain this, should be able to access via `http://localhost:8080/ipns/your.domain`, same for http gateways.

if you have slow dns, then use IPNS
- allows change site without changing dns record every time!!
- `ipfs name publish <hash>`
  - Using IPNS, important to think about assets could be loaded from 2 different resolved hashes when updating site
    - lead to outdates/missing assets unless accounted for!

- change DNS TXT record to `dnslink=/ipns/<peer id>`, wait for it to propagate then try accessing `http://localhost:8080/ipns/your.domain`

How to expose http://your.domain to other users (that use http only)
- edit TXT record
  - point A record of your.domain to IP address of an IPFS daemon that listens on port 80 (HTTP requests).
    - IPFS gateway will recognise your.domain as an IPNS name, so will save under `/ipns/your.domain/` instead of `/`
  - allow the DNS to propagate, then anyone should be able to access ipfs-hosted site without any extra configuration at `http://your.domain`

Alternative
- use CHAME records to point DNS records of the gateway.
- IP addresses of gateway are automatically updated
- CHAME records don't allow for other records (such as TXT to refer to IPFS/IPNS record)
  - because of this, IPFS allows to create a DNS TXT record for `_dnslink.your.domain` with `dnslink=/ipns/<your peer id>`

- CHAME for domain to gateway.ipfs.io and adding `_dnslink.your.domain` with `dnslink=/ipns/<your peer id>` = HOST SITE WITHOUT EXPLICITYLU REFFERING TO IP ADDRESSES OF IPFS GATEWAY
