# UWE Computer Science Final Year Project.<br/>Version Control System using IPFS.

This is my final year project that I have developed at the University of the West of England.
<br/>The idea was to create a peer-to-peer versioning system, thus (after some research) I found the Interplanetary File System (IPFS) a useful protocol/tool to use to develop this project.

----------

## What has been achieved
- command line
  - adding a file
  - creating a versioning file (.ipvc file)
    - file in json format
- GUI
  - adding a file
  - qr code for accessing file (HTTP/IPFS gateway) --> gimmick for project in progress event, so passer-by's can try out demo.
  - accessing file through IPFS daemon. (click on QR code).

## To be achieved (descending order of priority)
- [ ] ADD TESTING!

- command line
  - [ ] Adding a directory to IPFS
    - I think I have an issue with windows file system compared to linux/mac file system.
    - difficulty = 6/10 (worst case, maybe use terminal commands rather than API?)
  - [ ] Adding IPNS
    - Either add to versioning file or to whole directory input (head version).
    - **Need a discussion with supervisor about this.**
    - difficulty = 7/10
      - no idea how to do this with IPFS API's...
  - [ ] Adding validation checks on .ipvc files
    - make sure data is changed before using IPFS (no point otherwise)
    - hide the .ipvc file (so users cannot easily change it)
    - add checks that changing the name of a dir/file does not create new .ipvc file.
      - maybe notify user that `files are the same as X, do you want to use same ipvc file or create new versioning`
    - difficulty = 7/10 (need to do some research for this)
  - [ ] getting a file (similar to 'git clone')
    - difficulty = 5/10
      - shouldn't be too hard, just have to look at API code (no good docs for beginner).
      - get file via .ipvc (so returns head)
      - get file via a hash (so returns contents of hash)
  - [ ] adding branch support
    - difficulty 8/10
      - **need a discussion with supervisor about this.**
      - store all branches onto the .ipvc file?
  - [ ] web app support
    - difficulty 10/10
      - requires rebasing code to JS!
      - requires learning web development (which may take too long)
  - [ ] create server side version of code (HTTP/IPFS server)
    - difficulty 9/10
      - Can use the code I have already created
      - Just need to create a RESTful version of existing code -> could use default language (Java) I have already developed code for.
        - response from server in form of JSON?
        - how would you return large directories back to clients? Maybe store all the data onto server only (similar to client accessing via SSH?).
          - would be good for a web app support
          - client would be downloading with HTTP, defeats purpose of IPFS's peer-to-peer network... but does still retain IPFS's permanence.

----------

# Information about project

## Inspiration for application name
[thread of users talking the need for IPFS versioning](https://discuss.ipfs.io/t/history-versioning-of-documents-ipfs-ipns/564/6)
<br/>[github gist of an idea of versioning](https://gist.github.com/flyingzumwalt/a6821e843366d606aeb1ba53525b8669)


## explination of IPFS?
Todo:
- what is IPFS
- goals of IPFS
- ...

## installation
Todo:
- find out how to bundle IPFS API and program to allow users to install.
- show installation guides for IPFS
- show installation guides for application
  - hopefully provide Maven/Gradle support for dev installation.
  - bundle/.exe file for user installation.
    - need to find out how to bundle and add program to environment variables.

## documentation
Todo:
- need Java/Dev Documentation
- need User Documentation.
