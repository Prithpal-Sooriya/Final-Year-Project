/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class used to create the json code for versions.
 *
 * @author Prithpal Sooriya
 */
public class VersionJSONCreator {

  /* constants */
  private static final String JSONOBJECT_HEAD_KEY = "head"; //will return a JSONObject for the head
  private static final String JSONARRAY_VERSIONS_KEY = "versions"; //will return a JSONArray of all JSONObject versions
  private static final String JSONOBJECT_AUTHOR_KEY = "author"; //will return String for author name
  private static final String JSONOBJECT_COMMIT_KEY = "commitMessage"; //will return String for commit message
  private static final String JSONOBJECT_DATE_KEY = "date"; //will return String for the date
  private static final String JSONOBJECT_HASH_KEY = "hash"; //will return a json array of MerkleNodes
  private static final String JSONARRAY_BRANCHES_KEY = "branches"; //return json array for branches
  public static final String JSONOBJECT_BRANCH_MASTER_KEY = "master"; //an example of branch: master is default and cannot be removed

  /*
  json will be in format
{
  "branches": [{
    "master": {
      "head": "hash2",
      "versions": [{
        "date": "Tue Apr 03 19:13:21 BST 2018",
        "commitMessage": "initial commit",
        "author": "Prithpal Sooriya",
        "hash": "hash1"
      }, {
        "date": "Tue Apr 03 19:13:21 BST 2018",
        "commitMessage": "commit 2",
        "author": "Prithpal Sooriya",
        "hash": "hash2"
      }]
    }
  }, {
    "Development": {
      "head": "hash3",
      "versions": [{
        "date": "Tue Apr 03 19:13:21 BST 2018",
        "commitMessage": "Creating a development branch, that is a branch off the master branch",
        "author": "Prithpal Sooriya",
        "hash": "hash2"
      }, {
        "date": "Tue Apr 03 19:13:21 BST 2018",
        "commitMessage": "adding a commit to the dev branch",
        "author": "Prithpal Sooriya",
        "hash": "hash3"
      }]
    }
  }]
}
   */
  /**
   * initJSON() function used to create init json for versions file.
   *
   * @return String json string. Will return null if there is an error.
   */
  public static String initJSON() {
    JSONObject branches = new JSONObject(); //container for all the branches
    JSONArray branchesArr = new JSONArray(); //branches array

    //create new empty branch for master
    branchesArr.add(VersionJSONCreator.createBranch(JSONOBJECT_BRANCH_MASTER_KEY));

    //popuate the branches object with the branches array.
    branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);

    //return json string
    return branches.toJSONString();
  }

  /*
  public functions
  -----
  addCommit(json, hash, commitMessage, author, branchName)
    - adds a commit to a branch, returns json string
  addBranch(json, currentBranch, newBranch, commitMessage, author)
    - adds a new branch, returns json string
  getNumberOfBranches(json)
    - gets number of branches, returns int
  getNamesOfBranches(json)
    - gets names of branches, returns list of strings
  deleteBranch(json, branchToDelete)
    - removes a branch, returns new json string
      - will not delete if there is only 1 branch
      - will not delete master branch
  
   */
  /**
   * addCommit() adds a commit to json branch and version, as no branch name is
   * given it will master branch by default.
   *
   * @param json current json string
   * @param hash hash will be inputted from the ipfs network
   * @param commitMessage message for the new commit
   * @param author author name
   * @return String json string of versions. Will return null if there is an
   * error
   */
  public static String addCommit(String json, String hash, String commitMessage, String author) {
    return addCommit(json, hash, commitMessage, author, JSONOBJECT_BRANCH_MASTER_KEY);
  }

  /**
   * addCommit() adds a commit to json branch and version
   *
   * @param json current json string
   * @param hash (hash can be inputed from the ipfs network)
   * @param commitMessage message for new commit
   * @param author author name
   * @param branchName branch name
   * @return String json string of versions. Will return null if there is an
   * error
   */
  public static String addCommit(String json, String hash, String commitMessage, String author, String branchName) {

    if (branchName == null) {
      System.out.println("VersionJSONCreator - addCommit(): branchName given was null");
      return null;
    }

    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);
      JSONObject branchObject = searchJSONArrayBranches(branchesArr, branchName);
      if (branchObject == null) {
        System.out.println("VersionJSONCreator - addCommit(): branch " + branchName + " not found");
        return null;
      }
      //remove branch
      int index = branchesArr.indexOf(branchObject);
      branchesArr.remove(branchObject);

      //update head and versions
      JSONObject head = createJSONVersion(hash, commitMessage, author);
      JSONObject info = (JSONObject) branchObject.get(branchName);
      info.put(JSONOBJECT_HEAD_KEY, head); //head
      JSONArray versions = (JSONArray) info.get(JSONARRAY_VERSIONS_KEY);
//      System.out.println(versions);
      if (versions == null) {
        versions = new JSONArray();
      }
      versions.add(head);
      info.put(JSONARRAY_VERSIONS_KEY, versions);

      //add branch information back into branch
      branchObject.put(branchName, info);

      //add branch back into correct position
      branchesArr.add(index, branchObject);
      //update branches
      branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);

      //return json
      return branches.toJSONString();

    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.out.println("VersionJSONCreator - addCommit(): should not have reached this part of function");
    return null;
  }

  /**
   * addBranch() add a branch to the versions json
   *
   * @param json current json string
   * @param currentBranch string of current branch name
   * @param newBranch string name of new branch
   * @param commitMessage message of new commit
   * @param author name of author
   * @return json string to add branch. Will return null if there is an error
   */
  public static String addBranch(String json, String currentBranch, String newBranch, String commitMessage, String author) {
    //sanitise branch name/
    if (newBranch == null) {
      System.out.println("VersionJSONCreator - addBranch(): new branch name was null");
      return null;
    }
    if (newBranch.trim().isEmpty()) {
      System.out.println("VersionJSONCreator - addBranch(): new branch name was empty");
      return null;
    }
    if (currentBranch == null) {
      System.out.println("VersionJSONCreator - addBranch(): source branch was null");
      return null;
    }
    //prevent 2 branches with same name
    if (currentBranch.trim().equalsIgnoreCase(newBranch.trim())) {
      System.out.println("VersionJSONCreator - addBranch(): source branch was empty");
      return null;
    }

    //add new branch
    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);
      JSONObject currentBranchInfo = (JSONObject) VersionJSONCreator.searchJSONArrayBranches(branchesArr, currentBranch.trim()).get(currentBranch.trim());
      if (currentBranchInfo == null) {
        System.out.println("VersionJSONCreator - addBranch(): could not find source branch head");
        return null; //if the current branch does not exist return null
      }
      //head will be the new version just created
      //currentJSONVersion(string hash, string commitMessage, String author)
      JSONObject head = createJSONVersion(
        (String) ((JSONObject) currentBranchInfo.get(JSONOBJECT_HEAD_KEY)).get(JSONOBJECT_HASH_KEY),
        commitMessage,
        author
      );
      //create new branch
      JSONObject branch = VersionJSONCreator.createBranch(newBranch.trim(), head);
      branchesArr.add(branch);
      branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);

      return branches.toJSONString();

    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.out.println("VersionJSONCreator - addBranch(): should not have reached this part of function");
    return null;
  }

  /**
   * getNumberOfBranches() returns the number of branches in the json
   *
   * @param json current json from versions json
   * @return integer for the number of branches. Returns -1 if error
   */
  public static int getNumberOfBranches(String json) {
    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);

      //count number of branches
      return branchesArr.size();

    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("VersionJSONCreator - getNumberOfBranches(): should not have reached this part of function");
    return -1;
  }

  /**
   * getNamesOfBranches() gets a list of names (Strings) of the branches
   *
   * @param json current versions json
   * @return List<String> for all names of branches. Returns null if error
   */
  public static List<String> getNamesOfBranches(String json) {
    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);

      //return list of branch names
      return (List<String>) branchesArr.stream()
        .map(branch
          -> (String) ((JSONObject) branch)
          .keySet()
          .stream()
          .findFirst().get())
        .collect(Collectors.toList());
    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("VersionJSONCreator - getNamesOfBranches(): should not have reached this part of function");
    return null;
  }

  public static String mergeBranch(String json, String currentBranch, String destinationBranch, String commitMessage, String author, boolean deleteBranch) {
    //sanitation
    if (currentBranch == null) {
      System.out.println("VersionJSONCreator - mergeBranch(): source branch was null");
      return null;
    }
    if (destinationBranch == null) {
      System.out.println("VersionJSONCreator - mergeBranch(): destination branch was null");
      return null;
    }

    String newCommitMessage = "";
    String newAuthor = "";
    if (commitMessage != null) {
      newCommitMessage = commitMessage.trim();
    }
    if (author != null) {
      newAuthor = author.trim();
    }

    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);
      JSONObject currentBranchInfo = searchJSONArrayBranches(branchesArr, currentBranch);
      JSONObject destinationBranchInfo = searchJSONArrayBranches(branchesArr, destinationBranch);
      //check if branches exist
      if (currentBranchInfo == null) {
        System.out.println("VersionJSONCreator - mergeBranch(): source branch name does not exist");
        return null;
      }
      if (destinationBranchInfo == null) {
        System.out.println("VersionJSONCreator - mergeBranch(): destination branch name does not exist");
        return null;
      }

      //create new version
      //createJSONVersion(hash, commitMessage, author)
      JSONObject head = (JSONObject)((JSONObject) currentBranchInfo.get(currentBranch.trim())).get(JSONOBJECT_HEAD_KEY); //dev: {head: {..., hash:..}}
      String hash = (String) head.get(JSONOBJECT_HASH_KEY); //head:{...., hash:...}
//      System.out.println(hash);
      head = createJSONVersion(
        hash,
        newCommitMessage,
        newAuthor
      );
      
      //check if want to remove currentBranch
      if(deleteBranch && !currentBranch.trim().equals(JSONOBJECT_BRANCH_MASTER_KEY)) {
        branchesArr.remove(currentBranchInfo);
      }
      
      //update destination branch
      int indexOfDestination = branchesArr.indexOf(destinationBranchInfo);
      branchesArr.remove(destinationBranchInfo);
      
      //add the head to the destination branch (head and versions)
      JSONObject destinationBranchInfoHeader = (JSONObject)destinationBranchInfo.get(destinationBranch.trim()); //master:{head : {....}, versions:{....}}
      destinationBranchInfoHeader.put(JSONOBJECT_HEAD_KEY, head);
      JSONArray destinationBranchInfoVersions = (JSONArray) destinationBranchInfoHeader.get(JSONARRAY_VERSIONS_KEY);//versions:{....}
      destinationBranchInfoVersions.add(head);
      destinationBranchInfoHeader.put(JSONARRAY_VERSIONS_KEY, destinationBranchInfoVersions); //put versions into header
      destinationBranchInfo.put(destinationBranch.trim(), destinationBranchInfoHeader);
      
      branchesArr.add(indexOfDestination, destinationBranchInfo);

      //combine back to root json and return json string
      branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);
      return branches.toJSONString();
      
      
    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("VersionJSONCreator - mergeBranch(): should not have reached this part of function");
    return null;
  }

  /*
  private functions
  -----
  createBranch(branchName, [head : optional])
    - create a new branch, if JSONObject head not given, will keep it empty.
    - returns JSONObject, will return null if error
  deleteBranch(json, branchToDelete)
    - deletes a banch and returns new json, will return null if error
  createJSONVersion(hash, commitMessage, author)
    - creates a new JSONObject version --> used as new head and appended to versions array for branch
    - returns JSONObject, will return null if error
    - NOTE: maybe change this method name
  searchJSONArrayBranches(arr, branchKey)
    - searchs for a branch via its branchname
    - if found it will return it, else will return null
  
   */
  /**
   * createBranch() create a new branch with null/empty head and null/empty
   * version.
   *
   * @param branchName name of new branch
   * @return JSONObject of the new branch. Will return null if there is an
   * error.
   */
  private static JSONObject createBranch(String branchName) {
    return createBranch(branchName, null);
  }

  /**
   * createBranch() create a new branch
   *
   * @param branchName name of new branch
   * @param head JSONObject head for the branch head and latest
   * version(optional, if not will create a empty) branch)
   * @return JSONObject of the new branch. Will return null if there is an error
   */
  private static JSONObject createBranch(String branchName, JSONObject head) {
    //sanitise
    if (branchName == null) {
      System.out.println("VersionJSONCreator - createBranch(): branch name is null");
      return null;
    }

    JSONObject newHead = new JSONObject();
    JSONArray versions = new JSONArray();
    if (head != null) {
      newHead = head;
    }

    //store the branch information
    JSONObject info = new JSONObject();
    info.put(JSONOBJECT_HEAD_KEY, newHead);
    if (head != null) {
      versions.add(newHead);
    }
    info.put(JSONARRAY_VERSIONS_KEY, versions);

    //store the branch info into the branch
    JSONObject branch = new JSONObject();
    branch.put(branchName, info);

    return branch;
  }

  /**
   * deletebranch() deletes a branch
   *
   * @param json json string to change
   * @param branchToDelete branchName to delete
   * @return String result json string. It will return null if there is an error
   */
  private static String deleteBranch(String json, String branchToDelete) {
    //sanitation
    if (branchToDelete == null) {
      System.out.println("VersionJSONCreator - deleteBranch(): branch to delete was null");
      return null;
    }
    if (branchToDelete.equals(JSONOBJECT_BRANCH_MASTER_KEY)) {
      System.out.println("VersionJSONCreator - deleteBranch(): cannot delete master branch");
      return null;
    }
    if (getNumberOfBranches(json) == 1) {
      //cannnot delete if there is only 1 branch
      System.out.println("VersionJSONCreator - deleteBranch(): cannot delete branch as there is only 1 branch");
      return null;
    }

    try {
      JSONParser parser = new JSONParser();
      JSONObject branches = (JSONObject) parser.parse(json);
      JSONArray branchesArr = (JSONArray) branches.get(JSONARRAY_BRANCHES_KEY);
      JSONObject branch = searchJSONArrayBranches(branchesArr, branchToDelete);

      //remove branch and return new json
      if (branch == null) {
        //branch not found
        System.out.println("VersionJSONCreator - deleteBranch(): branch to delete (" + branchToDelete.trim());
        return null;
      }
      branchesArr.remove(branch);
      branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);
      return branches.toJSONString();

    } catch (ParseException ex) {
      Logger.getLogger(VersionJSONCreator.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.out.println("VersionJSONCreator - deleteBranch(): should not have reached this part of function");
    return null;
  }

  /**
   * createJSONVersion() create a new version for branch versions array
   *
   * @param hash hash of ipfs commmit
   * @param commitMessage details of the commit
   * @param author author of the commit
   * @return JSONObject json object of new version. Will return null if there is
   * an error
   */
  private static JSONObject createJSONVersion(String hash, String commitMessage, String author) {
    JSONObject version = new JSONObject();
    version.put(JSONOBJECT_AUTHOR_KEY, author);
    version.put(JSONOBJECT_COMMIT_KEY, commitMessage);
    version.put(JSONOBJECT_DATE_KEY, new Date().toString());
    version.put(JSONOBJECT_HASH_KEY, hash);
    return version;
  }

  /**
   * searchJSONArrayBranches() method used to search jsonarray of branches by
   * branch name.
   *
   * @param arr array of json branches
   * @param branchKey name of branch to search for
   * @return JSONObject of branch. Will return null if there is an error or if
   * the branch could not be found.
   */
  private static JSONObject searchJSONArrayBranches(JSONArray arr, String branchKey) {
    Optional<JSONObject> branch = arr.stream()
      .filter(jsonObj -> ((JSONObject) jsonObj).containsKey(branchKey))
      .findFirst();
    if (branch.isPresent()) {
      return branch.get();
    }
    System.out.println("VersionJSONCreator - searchJSONArrayBranches(): should not have reached this part of function");
    return null;
  }

  /**
   * main() main method for testing purposes only.
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    //init test
    System.out.println("Init test");
    String json = VersionJSONCreator.initJSON();
    System.out.println(json);
//    scan.next();

    String branchDevelopment = "Development";

    //add commit test
    System.out.println("Committing on master test");
    json = VersionJSONCreator.addCommit(json, "hash1", "initial commit", "Prithpal Sooriya");
    System.out.println(json);
//    scan.next();
    json = VersionJSONCreator.addCommit(json, "hash2", "commit 2", "Prithpal Sooriya");
    System.out.println(json);
//    scan.next();

    //add branch test
    System.out.println("Create branch 'Development' test");
    json = VersionJSONCreator.addBranch(
      json, VersionJSONCreator.JSONOBJECT_BRANCH_MASTER_KEY, branchDevelopment,
      "Creating a development branch, that is a branch off the master branch", "Prithpal Sooriya");
    System.out.println(json);
//    scan.next();

    //add commit on branch test
    System.out.println("Committing on Development branch test");
    json = VersionJSONCreator.addCommit(json, "hash3", "adding a commit to the dev branch", "Prithpal Sooriya", branchDevelopment);
    System.out.println(json);
//    scan.next();

    //number of branches test
    System.out.println("Number of branches test");
    System.out.println(VersionJSONCreator.getNumberOfBranches(json));

    //names of branches test
    System.out.println("Names of branches test");
    VersionJSONCreator.getNamesOfBranches(json).forEach(name -> System.out.println(name));

    //delete dev branch test
    System.out.println("Delete branches test");
//    System.out.println("deleting master: " + VersionJSONCreator.deleteBranch(json, VersionJSONCreator.JSONOBJECT_BRANCH_MASTER_KEY)); //should return null WORKS
    String json2 = VersionJSONCreator.deleteBranch(json, branchDevelopment);
    System.out.println(json2);
    System.out.println(VersionJSONCreator.getNumberOfBranches(json2));
    VersionJSONCreator.getNamesOfBranches(json2).forEach(name -> System.out.println(name));

    //merge branch test
    System.out.println("Merge branches test");
    json2 = VersionJSONCreator.mergeBranch(json, JSONOBJECT_BRANCH_MASTER_KEY, branchDevelopment, "merging dev to master and removing dev branch", "Prithpal Sooriya", true);
    System.out.println(json2);
    
  }

}
