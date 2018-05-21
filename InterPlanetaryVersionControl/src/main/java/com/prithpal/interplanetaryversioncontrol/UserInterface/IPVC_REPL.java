/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.UserInterface;

import com.prithpal.interplanetaryversioncontrol.core.IPFSWrapper;
import com.prithpal.interplanetaryversioncontrol.core.IPVC;
import java.io.File;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPVC_REPL {

  private static final String commandHelp = "--help";
  private static final String commandAdd = "add";
  private static final String commandCreateBranch = "create-branch";
  private static final String commandMergeBranch = "merge-branch";
  private static final String commandDeleteBranch = "delete-branch";
  private static final String commandPublish = "publish";
  private static final String commandGitPublish = "git-publish";

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    System.out.println("##################################");
    System.out.println("# InterPlanetary Version Control #");
    System.out.println("##################################");
    System.out.println("Type  '--help' for commands");

    boolean run = true;
    while (run) {
      String command = scan.nextLine();
      controller(command, scan);
    }

  }

  public static void controller(String command, Scanner scan) {
    IPVC ipvc = new IPVC(new IPFSWrapper());
    File f = null;
    switch (command) {
      case commandHelp:
        System.out.println("add: select a folder to add to ipfs. Will create a versioned file to hold versions");

        System.out.println("create-branch: allows creation of new branches");
        System.out.println("merge-branch: allows merging of branches, and if the merged branch should end (master branch will not be destroyed)");
        System.out.println("delete-branch: allows deletion of branches (master branch cannot be destroyed)");

        System.out.println("publish: select a ipvc folder to publish to ipns. All versions can be accessed from here");

        System.out.println("git-publish: select an existing git repository to add to ipfs");
        break;
      case commandAdd:
        f = fileChooser();
        if (f != null) {
          commandAddController(ipvc, f, scan);
        }
        break;
      case commandCreateBranch:
        f = fileChooser();
        if (f != null) {
          commandCreateBranchController(ipvc, f, scan);
        }
        break;
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

  private static void commandAddController(IPVC ipvc, File f, Scanner scan) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter Commit message");
    String commit = scan.nextLine();

    File ipvcFolder = ipvc.searchForIPVCDirectory(f);
    String branch = "master";
    if (ipvcFolder != null) {
      if (ipvcFolder.exists() && ipvcFolder.isDirectory()) {
        System.out.println("Select Branch");
        branch = scan.nextLine();
      }
    }
    String hash = ipvc.addIPFS(f, commit, author, branch);
  }

  private static void commandCreateBranchController(IPVC ipvc, File f, Scanner scan) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter Commit message");
    String commit = scan.nextLine();
    System.out.println("Enter branch to fork");
    String currentBranch = scan.nextLine();
    System.out.println("Enter new branch name");
    String newBranch = scan.nextLine();
    
    boolean success = ipvc.createBranch(f, currentBranch, newBranch, commit, author);
    if(success) {
      System.out.println("Branch successfully created");
    }
  }

  
}
