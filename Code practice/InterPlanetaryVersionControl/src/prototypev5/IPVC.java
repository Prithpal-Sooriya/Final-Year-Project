/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev5;

import java.io.BufferedReader;
import prototypev4.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Class to use IPVC methods
 *
 * @author Prithpal Sooriya
 *
 *
 */
public class IPVC {

  private static final String IPVC_FOLDER = "/.ipvc";
  private static final String COMMITS_JSON = "/.commits.json";
  private String ipnsHash = "";

  /**
   * IPVC() constructor that will start the IPFS daemon
   */
  public IPVC() {
    IPFS.startDaemon();
  }

  /**
   * close() allows closing of the daemon
   */
  public void close() {
    IPFS.stopDaemon();
  }

  /**
   * add() adds a folder to the IPFS network (plus creates .ipvc folder)
   *
   * @param f folder to add
   * @param commitMessage message of commit
   * @param author author of commit NOTE: maybe return a boolean if success or
   * not
   */
  public String add(File f, String commitMessage, String author, String branchName) {
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
    //create the folder if it does not exist
    boolean firstCommit = !ipvcLocation.exists();
    if (firstCommit) {
      try {
        initIPVC(ipvcLocation);
      } catch (IOException ex) {
        System.err.println("IPVC - add(): Issue when creating IPVC folder");
        Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //write hash to commits json
    String hash = IPFS.add(f, false);
    File commitsJSON = new File(ipvcLocation.getAbsolutePath() + COMMITS_JSON);
    try {
      String json = firstCommit
        ? VersionJSONCreator.addCommit(
          readFile(commitsJSON),
          hash, commitMessage, author)
        : VersionJSONCreator.addCommit(
          readFile(commitsJSON),
          hash, commitMessage, author, branchName);

      writeFile(commitsJSON, json);
    } catch (IOException ex) {
      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    }

    return hash;

  }

  /**
   * addIPVC() will ipfs add -r the ipvc folder and create an ipns hash to use
   *
   * @param f the project directory (which will contain .ipvc directory).
   * @return String ipns static hash (for speed improvement, store the ipns
   * hash)
   */
  private String addIPNS(File f) {
    File ipvcFolder = new File(f.getAbsolutePath() + IPVC_FOLDER);
    String hash = IPFS.add(ipvcFolder, true);
    ipnsHash = IPNS.add(hash);
    return ipnsHash;
  }

  /**
   * initIPVC(File f) initialises the IPVC folder
   *
   * @param f location of ipvc directory
   */
  private void initIPVC(File f) throws IOException {
    boolean makeFolder = f.mkdir();
    if (makeFolder) {
//      System.out.println("Folder made");
      Path path = f.toPath();
      Files.setAttribute(path, "dos:hidden", true);

//      System.out.println(path.toString());
      File commits = new File(path.toString() + "/.commits.json");
      commits.createNewFile();
      createCommitsJSON(commits);
    } else {
      System.err.println("IPVC - initIPVC(): Could not make IPVC folder at location " + f.getAbsolutePath());
    }
  }

  /**
   * createCommitsJSON() creates the .commits.json file.
   *
   * @param f the commits.json file
   */
  private void createCommitsJSON(File f) throws IOException {
    String json = VersionJSONCreator.initJSON();
    writeFile(f, json);
  }

  /**
   * writeFile() writes a string to a file (no appending)
   *
   * @param f file to write to
   * @param s string to write
   */
  private void writeFile(File f, String s) throws IOException {
    FileWriter fw = new FileWriter(f);
    fw.write(s);
    fw.flush();
    fw.close();
  }

  /**
   * readFile() reads the file give and returns string
   *
   * @param f file to read
   * @return string of content from file
   */
  private String readFile(File f) throws IOException {
    StringBuilder sb = new StringBuilder();
    Files.readAllLines(f.toPath()).forEach(line -> sb.append(line));
    return sb.toString();
  }

  //main method used for testing purposes
  public static void main(String[] args) {
    //scanner for input
    Scanner scan = new Scanner(System.in);

    //create ipvc (it will start daemon)
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
      String hash = ipvc.add(file, commit, author, VersionJSONCreator.JSONOBJECT_BRANCH_MASTER_KEY);
      if (hash != null) {
        System.out.println("Add was successful!");
        System.out.println("IPFS Hash:");
        System.out.println(hash);

        System.out.println("Updating IPNS");
        String ipnsHash = ipvc.addIPNS(file);
        System.out.println("IPNS Hash:");
        System.out.println(ipnsHash);
      } else {
        System.out.println("Add was not successful");
      }
    } else {
      System.out.println("Add cancelled");
    }

    //close ipvc and thus close daemon --> may be better to close somewhere else...
    ipvc.close();

    System.exit(0);

  }

}
