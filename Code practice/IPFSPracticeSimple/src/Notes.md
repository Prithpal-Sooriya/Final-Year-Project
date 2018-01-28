This is where the functionality will be created

Doing
- adding file
  - ipfs add file will create list of merkle nodes
  - nodes will be added to versionedFile (large files will have more than 1 node) --> EDIT, large files will be given 1 absolute node!
    - new line in file = new version

Issues
- adding folders
  - ipfs add folder is supported
  - will give a list of merkle nodes (need to find out which node is the root folder/node)
  - add versionedFile INTO the root node
    - new line in file = new version 

  - when adding folder
    - could note down all merkel nodes (to get maximum clarity of ALL files changed)
      - meaning this file will be really large -> also how to structure when reading? maybe root node = first hash in file?
    - store just root node (looses what files where specifically changed)

Other ideas
- store versionedFiles as JSON!!
  - it will make my life a lot easier!!


--------------
EDIT
- all large files will have a root node (which has compiled node info in)
- all folders will have a root node (where data can be accessed)

- Info can be accessed using ipfs.dag.get(hash) or ipfs.object.get