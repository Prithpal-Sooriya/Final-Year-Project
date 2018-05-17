/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prithpal
 */
public class IPVC {

  private static final String IPVC_FOLDER = "/.ipvc";
  private static final String COMMITS_JSON = "/.commits.json";
  private String ipnsHash = "";
  private IPFSWrapper ipfs;

  public IPVC(IPFSWrapper ipfs) {
    this.ipfs = ipfs;
  }

  public String add(File f, String commitMessage, String author, String branchName) {
    //TODO: change error messages to Logger.error (utilise the error class)
    if (f == null) {
      System.err.println("IPVC - add(): folder given was null");
      return null;
    }
    if (!f.exists()) {
      System.err.println("IPVC - add(): folder given does not exist");
      return null;
    }
    if (!f.isDirectory()) {
      System.err.println("IPVC - add(): folder given was not a directory");
      return null;
    }

    File ipvcLocation = new File(f.getAbsolutePath() + IPVC_FOLDER);
    boolean firstCommit = !ipvcLocation.exists();
    if (firstCommit) {
      try {
        initIPVC(ipvcLocation);
      } catch (IOException ex) {
        System.err.println("IPVC - add(): Issue when creating IPVC folder");
      }
    }

    //return is in form [hash](url)
    String result = ipfs.addRecursiveFilesToIPFS(f, false);
    String hash = ipfs.getHashFromIPFSAdd(result);
    File commitsJSON = new File(ipvcLocation.getAbsolutePath() + COMMITS_JSON);
    try {
      String json = firstCommit
      ? VersionJSONCreator.addCommit(
      FileUtilities.readFile(commitsJSON),
      hash, commitMessage, author)
      : VersionJSONCreator.addCommit(
      FileUtilities.readFile(commitsJSON),
      hash, commitMessage, author, branchName);
      
      FileUtilities.writeFile(commitsJSON, json);
    } catch (IOException ex) {
      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return hash;
  }

  //TODO need a public method for handling/abstracting IPNS
  
  private void initIPVC(File f) throws IOException {
    if (f.mkdir()) {
      Path path = f.toPath();
      Files.setAttribute(path, "dos:hidden", true);

      File commits = new File(path.toString() + "/.commits.json");
      commits.createNewFile();
      createCommitsJSON(commits);
    } else {
      System.err.println("IPVC - initIPVC(): Could not make IPVC folder at location " + f.getAbsolutePath());
    }
  }

  private void createCommitsJSON(File f) throws IOException {
    String json = VersionJSONCreator.initJSON();
    FileUtilities.writeFile(f, json);
  }

}
