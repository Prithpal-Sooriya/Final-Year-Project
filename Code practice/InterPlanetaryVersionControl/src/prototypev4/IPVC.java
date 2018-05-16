/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import jdk.jfr.events.FileWriteEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPVC {

  private static final String JSONOBJECT_HEAD_KEY = "head"; //will return a JSONObject for the head
  private static final String JSONARRAY_VERSIONS_KEY = "versions"; //will return a JSONArray of all JSONObject versions
  private static final String JSONOBJECT_AUTHOR_KEY = "author"; //will return String for author name
  private static final String JSONOBJECT_COMMIT_KEY = "commitMessage"; //will return String for commit message
  private static final String JSONOBJECT_DATE_KEY = "date"; //will return String for the date
  private static final String JSONOBJECT_HASH_KEY = "hash"; //will return a json array of MerkleNodes
  private static final String JSONARRAY_BRANCHES_KEY = "branches"; //return json array for branches
  private static final String JSONOBJECT_BRANCH_MASTER_KEY = "master"; //an example of branch: master is default and cannot be removed

  public IPVC() {
//    IPFS.startDaemon();
    //if daemon is already started, it will not affect anything.
  }

  public void close() {
    IPFS.stopDaemon();
  }

  public void add(File f, String commitMessage, String author) {
    //default is to add on master branch
    add(f, commitMessage, author, JSONOBJECT_BRANCH_MASTER_KEY);
  }

  public void add(File f, String commitMessage, String author, String branch) {
    //sanitation on file
    if (f == null) {
      return;
    }
    if (!f.exists()) {
      return;
    }
    if (!f.isDirectory()) {
      return;
    }

    File ipvcLocation = new File(f.getAbsolutePath() + "/.ipvc");
    if (!ipvcLocation.exists()) {
      try {
        initIPVC(ipvcLocation);
      } catch (IOException ex) {
        System.err.println("Issue when creating IPVC folder");
        Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //add file to ipfs and update versions
    String head = IPFS.add(f);
    if (head == null) {
      System.err.println("IPVC add: issue when adding folder - hash was null");
      return;
    }
  }

  private void initIPVC(File f) throws IOException {
    boolean makeFolder = f.mkdir();
    if (makeFolder) {
      System.out.println("folder made");
      Path path = f.toPath();
      Files.setAttribute(path, "dos:hidden", true);

      System.out.println(path.toString());
      File commits = new File(path.toString() + "/commits");

      //create commits file
      commits.createNewFile();
      createJSONFileInformation(commits);
    } else {
      System.err.println("Could not make IPVC folder at location: " + f.getAbsolutePath());
    }
  }

  private void createJSONFileInformation(File f) throws IOException {
    JSONObject branches = new JSONObject();
    JSONArray branchesArr = new JSONArray(); //branch array
    JSONObject branchMaster = new JSONObject(); //create master branch
    JSONObject info = new JSONObject(); //create info for branch.

    //put branch head and vesions into info
    info.put(JSONOBJECT_HEAD_KEY, "");
    JSONObject versions = new JSONObject();
    info.put(JSONARRAY_VERSIONS_KEY, versions);

    //put info into branchMaster
    branchMaster.put(JSONOBJECT_BRANCH_MASTER_KEY, info);
    //put info into the branches array
    branchesArr.add(branchMaster);
    //put branches array into branches object (makes it easier for users to read)
    branches.put(JSONARRAY_BRANCHES_KEY, branchesArr);

    //write to file
    FileWriter fw = new FileWriter(f);
    fw.write(branches.toJSONString());
    fw.flush();
    fw.close();
  }
  
  private void addBranch(File f, String newBranchName, JSONObject parentBranch) {
    try {
      //get branches array
      FileReader reader = new FileReader(f);
      JSONParser parser = new JSONParser();
      JSONObject branchesObj = (JSONObject) parser.parse(reader);
      JSONArray branchesArr = (JSONArray) branchesObj.get(JSONARRAY_BRANCHES_KEY);
      
      //get contents of head from parent branch
      JSONObject parentHead = (JSONObject) parentBranch.get(JSONOBJECT_HEAD_KEY);
      String hash = (String) parentHead.get(JSONOBJECT_HASH_KEY);
      String commitMessage = (String) parentHead.get(JSONOBJECT_COMMIT_KEY);
      String author = (String) parentHead.get(JSONOBJECT_AUTHOR_KEY);
      
      //add head for newBranch
      JSONObject newBranch = new JSONObject();
      JSONObject version = createJSONVersion(hash, commitMessage, author);
      newBranch.put(JSONOBJECT_HEAD_KEY, version);
      
      //add version to new branch versions array
      JSONArray newBranchVersions = new JSONArray();
      newBranchVersions.add(version);
      newBranch.put(JSONARRAY_VERSIONS_KEY, newBranchVersions);
      
      //place versions into array
      branchesArr.add(newBranch);
      
      //save new contents
      branchesObj.put(JSONARRAY_BRANCHES_KEY, branchesArr);
      
      FileWriter fw = new FileWriter(f);
      fw.write(branchesObj.toJSONString());
      fw.flush();
      fw.close();
    } catch (IOException ex) {
      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ParseException ex) {
      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private JSONObject createJSONVersion(String hash, String commitMessage, String author) {
    JSONObject version = new JSONObject();
    version.put(JSONOBJECT_AUTHOR_KEY, author);
    version.put(JSONOBJECT_COMMIT_KEY, commitMessage);
    version.put(JSONOBJECT_HEAD_KEY, new Date().toString());
    version.put(JSONOBJECT_HASH_KEY, hash);
    return version;
  }
  
  private JSONObject updateBranchContents(JSONObject branch, String head) {
    return null;
  }
  

  //main method for testing purposes
  public static void main(String[] args) {
    //scanner for input
    Scanner scan = new Scanner(System.in);

    IPVC ipvc = new IPVC();
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select folder to add");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      System.out.println("Enter author:");
      String author = scan.nextLine();
      System.out.println("Enter Commit message");
      String commit = scan.nextLine();
      ipvc.add(file, commit, author);
    } else {
      System.out.println("Add cancelled");
    }
  }

}
