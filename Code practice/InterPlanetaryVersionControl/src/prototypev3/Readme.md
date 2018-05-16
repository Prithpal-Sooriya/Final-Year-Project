# prototype v3.

## plan
- this version will try to utilise communication with the IPFS daemon itself (may face less issues this way!)#
  - all the API's require the daemon to be running and communicate with the daemon via the API calls.

### scrum 1

- get 'add' file working by talking to the daemon directly
- get 'add' folder working by talking to the daemon directly
- get 'get' hash working by talking to the daemon directly
  - user will select location for the contents to be placed.

END OF SCRUM

- notes:
  - may be best to create a separate class that will handle all terminal commands
    - pass in commands (which are constants)
  - ipfs add -r is now working fully!! we can now focus on full development!
  - ipfs get working fully!
  - 