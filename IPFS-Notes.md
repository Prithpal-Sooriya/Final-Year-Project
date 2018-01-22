https://flyingzumwalt.gitbooks.io/decentralized-web-primer/content/
- useful for notes/tutorials for IPFS

IPFS files can be sent using:
  - IFPS/Daemon (so will need to install IPFS)
  - IPFS-JS (so info can be sent and received using a modern web browser)

Other ways to send data using IPFS
  - desktop app being developed
  - web browser support (not much support yet)
  - web browser extension support=

IPFS files can be accessed using:
  - localhost gateway (requires daemon to be running)
  - HTTP/IPFS gateway (doesn't require daemon, but is slower)
    - ipfs.io/ipfs/<hash>
    - ipfs.io/ipns/<hash>
  - Other IPFS gateways (does not require daemon running)
    - e.g. http://dweb.link/ipfs/Qme2sLfe9ZMdiuWsEtajWMDzx6B7VbjzpSC2VWhtB6GoB1/wiki/Anasayfa.html

Files (that are cached I think) can be mounted --> FUSE
  - means that files that were "online" can now be accessed on machine!

https://flyingzumwalt.gitbooks.io/decentralized-web-primer/content/avenues-for-access/lessons/power-of-content-addressing.html
- good for benefits of IPFS


- can use curl on the daemon running
  - queries the daemon
  - `curl http://localhost:5001/version`
  - queries can give info back (from the daemon) in the form of json
    - look at ipfs api commands :D
  - `curl http://localhost:5001/api/v0/ls?arg=<hash>` --> will output ls in form of json

DHT is used on IPNS
- DHT will hold hash of public key, and the publisher will sign the public key with their private key private key
  - so when we update the dir/file hash, then by signing the public key again to update
