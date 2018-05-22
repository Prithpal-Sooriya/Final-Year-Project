/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.UserInterface;

import com.prithpal.interplanetaryversioncontrol.beans.VersionBean;
import com.prithpal.interplanetaryversioncontrol.core.FileUtilities;
import com.prithpal.interplanetaryversioncontrol.core.IPFSWrapper;
import com.prithpal.interplanetaryversioncontrol.core.IPVC;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPVC_REPL {

  private static final String SETTINGS_CURRENT_PROJECT = "currentProject.flag";
  private static String currentProjectPath = null;
  //commands
  private static final String commandHelp = "--help"; //done
  private static final String commandChooseProject = "choose-project"; //done
  private static final String commandAdd = "add"; //done
  private static final String commandGet = "get";
  private static final String commandCreateBranch = "create-branch"; //done
  private static final String commandMergeBranch = "merge-branch"; //done
  private static final String commandDeleteBranch = "delete-branch"; //done
  private static final String commandViewHead = "view-branch-head"; //done
  private static final String commandViewVersions = "view-branch-versions"; //done
  private static final String commandPublishIPFS = "publish-ipfs"; //done
  private static final String commandPublishIPNS = "publish-ipns"; //done
  private static final String commandGitPublish = "git-publish"; //done

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    //on start up load previous project tracked
    try {
      File projectCacheFile = new File(new File(FileUtilities.getSettingsDirectory()), SETTINGS_CURRENT_PROJECT);
      currentProjectPath = FileUtilities.readFile(projectCacheFile);
    } catch (IOException ex) {
      System.err.println("IPVC_REPL - main(): could not read project cache file");
    }

    System.out.println("##################################");
    System.out.println("# InterPlanetary Version Control #");
    System.out.println("##################################");
    System.out.println("Type  '--help' for commands");

    boolean run = true;
    while (run) {
      if (!validateCurrentProject()) {
        currentProjectPath = null;
      }
      if (currentProjectPath != null) {
        System.out.println("Current Project working on: " + currentProjectPath);
      } else {
        System.out.println("Need to select on a folder to work on with the 'choose-project' command");
      }
      String command = scan.nextLine();
      controller(command, scan);
    }

  }

  public static void controller(String command, Scanner scan) {
    IPVC ipvc = new IPVC(new IPFSWrapper());
    File f = null;
    if(validateCurrentProject()) {
      f = new File(currentProjectPath);
    }
    switch (command) {
      case commandHelp:
        System.out.println("IPVC commands");
        System.out.println("choose-project: choose a project to work on, can be changed");
        System.out.println("add: select a folder to add to ipfs. Will create a versioned file to hold versions");
        System.out.println("get: get a folder from ipfs network. Requires hash");
        
        System.out.println("create-branch: allows creation of new branches");
        System.out.println("merge-branch: allows merging of branches, and if the merged branch should end (master branch will not be destroyed)");
        System.out.println("delete-branch: allows deletion of branches (master branch cannot be destroyed)");
        System.out.println("view-branch-head: view the content (date, author, commit message, hash) of a branch");
        System.out.println("view-branch-versions: view the content (date, author, commit message, hash of a branches versions");

        System.out.println("publish-ipns: select an ipvc folder to publish to ipns. All versions can be accessed from here");
        System.out.println("publish-ipfs: select an ipvc folder to publish to ipfs. All versions can be accessed from here");

        System.out.println("git-publish: select an existing git repository to add to ipfs");
        break;
      case commandChooseProject:
        commandChooseProjectController();
        break;
      case commandAdd:
        if (f.exists()) {
          commandAddController(ipvc, f, scan);
        }
        break;
      case commandGet:
        commandGetController(ipvc, scan);
        break;
      case commandCreateBranch:
        if (f != null) {
          commandCreateBranchController(ipvc, f, scan);
        }
        break;
      case commandDeleteBranch:
        if (f != null) {
          commandDeleteBranchController(ipvc, f, scan);
        }
        break;
      case commandMergeBranch:
        if (f != null) {
          commandMergeBranchController(ipvc, f, scan);
        }
        break;
      case commandViewHead:
        if (f != null) {
          commandViewHeadController(ipvc, f, scan);
        }
        break;
      case commandViewVersions:
        if (f != null) {
          commandViewVersionsController(ipvc, f, scan);
        }
        break;
      case commandPublishIPFS:
        if (f != null) {
          commandPublishIPFSController(ipvc, f, scan);
        }
        break;
      case commandPublishIPNS:
        if (f != null) {
          commandPublishIPNSController(ipvc, f, scan);
        }
        break;
      case commandGitPublish:
        if (f != null) {
          commandGitPublishController(ipvc, f, scan);
        }
      default:
        System.out.println("The command given was not recognised. Please reference back to '--help' command");
    }
  }

  private static File fileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select folder to add");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    } else {
      System.out.println("choose directory cancelled");
    }
    return null;
  }

  private static void commandChooseProjectController() {
    File f = fileChooser();
    try {
      File projectCacheFile = new File(new File(FileUtilities.getSettingsDirectory()), SETTINGS_CURRENT_PROJECT);
      FileUtilities.writeFile(projectCacheFile, f.getCanonicalPath());
      currentProjectPath = f.getCanonicalPath();
    } catch (IOException ex) {
      System.err.println("IPVC_REPL - main(): could not read project cache file");
    }
  }

  private static boolean validateCurrentProject() {
    if (currentProjectPath == null) {
      return false;
    }
    if (currentProjectPath.trim().isEmpty()) {
      return false;
    }
    if (!new File(currentProjectPath).exists()) {
      return false;
    }
    return true;
  }

  private static void commandAddController(IPVC ipvc, File f, Scanner scan) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter Commit message");
    String commit = scan.nextLine();

    File ipvcFolder = ipvc.searchForIPVCDirectory(f);
    String branch = "master";
    if (ipvcFolder != null) {
      if (ipvcFolder.exists() && ipvcFolder.isDirectory()) {
        List<String> branchNames = ipvc.getBranchNames(f);
        System.out.println("Select Branch (" + String.join(",", branchNames) + ")");
        branch = scan.nextLine();
      }
    }
    String hash = ipvc.addIPFS(f, commit, author, branch);
  }

  private static void commandGetController(IPVC ipvc, Scanner scan) {
    System.out.println("Select location to add content");
    File f = fileChooser();
    System.out.println("Provide hash:");
    String hash = scan.nextLine();
    System.out.println("Provide a name (if non given, then using hash as name)");
    String name = scan.nextLine();
    
    if(name.trim().isEmpty()) {
      name = null;
    }
    if(ipvc.getFromIPFS(f, hash, name)) {
      System.out.println("Success getting file");
    }
    else {
      System.out.println("Not able to get file (is hash correct?)");
    }
  }
  
  private static void commandCreateBranchController(IPVC ipvc, File f, Scanner scan) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter Commit message");
    String commit = scan.nextLine();
    
    List<String> branchNames = ipvc.getBranchNames(f);
    
    System.out.print("Enter branch to fork (" + String.join(",", branchNames) + ")");
    String currentBranch = scan.nextLine();
    System.out.println("Enter new branch name");
    String newBranch = scan.nextLine();

    boolean success = ipvc.createBranch(f, currentBranch, newBranch, commit, author);
    if (success) {
      System.out.println("Branch successfully created");
    }
  }

  private static void commandDeleteBranchController(IPVC ipvc, File f, Scanner scan) {
    List<String> branchNames = ipvc.getBranchNames(f);
    System.out.println("Enter branch to delete: (" + String.join(",", branchNames) + ")");
    String branch = scan.nextLine();

    if (ipvc.deleteBranch(f, branch)) {
      System.out.println("Delete successful");
    }
  }

  private static void commandMergeBranchController(IPVC ipvc, File f, Scanner scan) {
    List<String> branchNames = ipvc.getBranchNames(f);
    System.out.println("Enter branch to merge: (" + String.join(",", branchNames) + ")");
    String sourceBranch = scan.nextLine();
    System.out.println("Enter branch to merge to: (" + String.join(",", branchNames) + ")");
    String destinationBranch = scan.nextLine();
    System.out.println("Enter commit message:");
    String commitMessage = scan.nextLine();
    System.out.println("Enter author:");
    String author = scan.nextLine();

    boolean delete = false;
    while (true) {
      System.out.println("Do you want to delete the merging branch? (Y/N):");
      String input = scan.nextLine().trim();

      if (input.equalsIgnoreCase("Y")) {
        delete = true;
        break;
      } else if (input.equalsIgnoreCase("N")) {
        delete = false;
        break;
      } else {
        System.out.println("Input of type 'Y'/'N'");
      }
    }

    boolean success = ipvc.mergeBranch(f, sourceBranch, destinationBranch, commitMessage, author, delete);
    if (success) {
      System.out.println("merge success");
    } else {
      System.out.println("merge failure");
    }
  }

  private static void commandViewHeadController(IPVC ipvc, File f, Scanner scan) {
    List<String> branchNames = ipvc.getBranchNames(f);
    System.out.println("Select a branch to view:(" + String.join(",", branchNames) + ")");
    String branch = scan.nextLine();

    VersionBean version = ipvc.getLatestVersion(f, branch.trim());
    String content
            = version.getDate() + "\n"
            + version.getCommitMessage() + " - " + version.getAuthor() + "\n"
            + "ipfs hash: " + version.getHash();

    System.out.println(branch.trim() + " head content:\n");
    System.out.println(content);
  }

  private static void commandViewVersionsController(IPVC ipvc, File f, Scanner scan) {
    List<String> branchNames = ipvc.getBranchNames(f);
    System.out.println("Select a branch to view:(" + String.join(",", branchNames) + ")");
    String branch = scan.nextLine();

    System.out.println(branch.trim() + " version history");
    List<VersionBean> versions = ipvc.getHistory(f, branch.trim());
    for (VersionBean version : versions) {
      String content
              = version.getDate() + "\n"
              + version.getCommitMessage() + " - " + version.getAuthor() + "\n"
              + "ipfs hash: " + version.getHash();
    }
  }

  private static void commandPublishIPFSController(IPVC ipvc, File f, Scanner scan) {
    String hash = ipvc.addIPVCToIPFS(f, true);
    System.out.println("IPVC hash on IPFS network: " + hash);
  }

  private static void commandPublishIPNSController(IPVC ipvc, File f, Scanner scan) {
    String ipfsHash = ipvc.addIPVCToIPFS(f, false);
    String ipvcHash = ipvc.addIPVCToIPNS(ipfsHash);
  }

  private static void commandGitPublishController(IPVC ipvc, File f, Scanner scan) {
    try {
      String hash = ipvc.addGitIPFS(f);
      System.out.println("git project is now clonable through this IPFS hash: " + hash);
    } catch (IOException ex) {
      Logger.getLogger(IPVC_REPL.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPVC_REPL.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
