Prototype 4

now that most of the IPFS commands work, I can now create a working cmd application

what to do now:
- make ipvc folder hidden
- support version branching
  - add branch
    - enter name for branch
    - branch will be the head node of json.
  - delete branch
    - enter name of branch to delete
    - will delete the whole contents of the branch.
    - cannot delete branch if there is only 1 branch remaining.
  - merge branch
    - allow user to decide if they want to keep sub-branch after merge or not
    - just have to rewrite the head of merged branch
  - list branches
    - list all the available branches
  - ipfs add onto a certain branch

END OF SPRINT
- completed:
  - added ipvc folder (hidden)
  - added json file.

- next sprint:
  - finish of json CRUD
    - create a seperate class to handle json.
  - create a UI (cmd version) will not be too bad