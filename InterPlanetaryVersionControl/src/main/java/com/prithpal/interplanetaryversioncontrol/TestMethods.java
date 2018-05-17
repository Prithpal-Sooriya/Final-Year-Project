/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import com.prithpal.interplanetaryversioncontrol.UserInterface.MainUI;
import core.IPFSWrapper;
import core.IPVC;
import core.VersionJSONCreator;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.json.simple.JSONObject;

/**
 *
 * @author Prithpal
 */
public class TestMethods {

  private static void testEnvironmentVariables() {
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      System.out.format("%s=%s%n",
              envName,
              env.get(envName));
    }
  }

  public static void main(String[] args) throws IOException {
//    System.out.println(System.getProperty("java.class.path"));
//    System.out.println(System.getenv("PATH"));
//    System.out.println(System.getProperty("user.dir"));
//    System.out.println(new File(".").getCanonicalPath());
    Scanner scan = new Scanner(System.in);
    JFrame mainFrame = new MainUI();
    IPFSWrapper ipfs = new IPFSWrapper(mainFrame);
    IPVC ipvc = new IPVC(ipfs);

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
        String ipnsResult = ipfs.addToIPNS(hash);
//        String ipnsHash = ipvc.addIPNS(file);
        System.out.println("IPNS Hash:");
        System.out.println(ipnsResult);
      } else {
        System.out.println("Add was not successful");
      }
    } else {
      System.out.println("Add cancelled");
    }

    System.exit(0);

  }
}
