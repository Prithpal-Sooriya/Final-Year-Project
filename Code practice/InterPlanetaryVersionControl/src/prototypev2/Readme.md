# IPVC prototype 2

- This version will use a new codebase for IPFS-API
  - using IPFS-HTTP API rather than the IPFS-Java API
 
- reasons for changing API
  - Java API was still being constructed
    - the API changed twice under development of this project
    - some of the functions in the Java API did not work correctly
      - e.g. IPFS-Get Folders did not seem to work
    - some of the functionality of IPFS was not provided on this API
      - e.g. recursive looping of IPFS-Add was not implemented = could not add whole folders.

  - HTTP API benefits:
    - fully constructed API
    - one of the main API's to use (like Golang and Javascript).
    - has alot of documentation.
    - information returned if in JSON format (easy to use/manipuate outputs)
    - ...

  - HTTP API drawbacks
    - requires the IPFS-Daemon to be running in the background
      - Java API also requires this
      - SOLUTION: try to run the daemon script via Java?
    
----------------------------

# end of scrum

## notes and observations from this version:
- positives:
  - figured out how to start and stop daemon on startup!
- negatives:
  - IPFS-HTTP-API is very useful, however terrible to use!
    - the documentation is in 'limbo' and different commands and flags are deprecated/updated.
    - documentation only explains how to use the HTTP via curl
      - and even with curl, I could only make it work with single files (no dirs)!