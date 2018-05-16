/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPFS {

  /*IPFS commands*/
//  private static final String IPFS_INIT= "ipfs init";
//  private static final String IPFS_ADD= "ipfs add -r";
//  private static final String IPFS_GET= "ipfs get";
 /*------------------------*/
 /* init commands for IPFS */
 /*------------------------*/
  /**
   * checks if there is a .ipfs directory for the user
   *
   * @return boolean: true if is does exist, false if it doesn't
   */
  public static boolean isInit() {
    String IPFSDir = System.getProperty("user.home") + "/.ipfs";
    File IPFSFolder = new File(IPFSDir);
    return IPFSFolder.exists();
  }

  /**
   * calls 'ipfs init' command to initialise IPFS on the clients system.
   */
  public static void init() {
    String command = "ipfs init";
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      //read outputs and errors
//      outputProcess(p);
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

 /*------------------------*/
 /* add command for IPFS */
 /*------------------------*/
  /**
   * Add a FOLDER to ipfs, as it will not be as useful than just adding a file.
   *
   * @param f: File of the folder to add to IPFS
   * @return String: hash of the root folder.
   */
  public static String add(File f) {
    if (f == null) {
      System.out.println("IPFS add: folder given was null");
      return null;
    }
    if (!f.exists()) {
      System.out.println("IPFS add: folder doesn't exit");
      return null;
    }

    //-r is recursive, -p is show progress, -Q is show only last hash
    String command = "ipfs add -r -Q " + '"' + f.getAbsolutePath() + '"';
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      //output to user % done
//      BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String lastLine = "";
      String currentLine;
//      while (!hasProcessTerminated(p)) {
//
//        while ((currentLine = error.readLine()) != null) {
//          lastLine = currentLine;
//        }
//
//        //output just last line
//        System.out.println(lastLine);
//        Thread.sleep(100);
//      }

      //now done, get the root hash
      System.out.println("Output:");
//      while ((currentLine = error.readLine()) != null) {
//        lastLine = currentLine;
//      }
      BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
      while ((currentLine = output.readLine()) != null) {
        lastLine = currentLine;
      }
      System.out.println(lastLine);
      //return the hash
      return lastLine;

    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

    //should not reach here if everything goes well!
    return null;
  }

 /*------------------------*/
 /* get command for IPFS */
 /*------------------------*/
  public static boolean get(File pathToAdd, String hash) {
    return get(pathToAdd, hash, null);
  }

  public static boolean get(File pathToAdd, String hash, String newDirName) {
    if (pathToAdd == null) {
      System.err.println("get: pathToAdd is null");
      return false;
    }
    if (!pathToAdd.exists()) {
      System.err.println("get: pathToAdd does not exist");
      return false;
    }
    if (!pathToAdd.isDirectory()) {
      System.err.println("get: pathToAdd is not a directory");
      return false;
    }

    String command = "ipfs get " + hash;
    try {
      //.exec(command, environment vars, path to run from)
      Process p = Runtime.getRuntime().exec(command, null, pathToAdd);
      p.waitFor();
      outputProcess(p);

      //rename the output dir if needed
      if (newDirName != null) {
        String path = pathToAdd.getAbsolutePath() + "/" + hash;
        File file = new File(path);
        File newFile = new File(pathToAdd.getAbsolutePath() + "/" + newDirName);
        file.renameTo(newFile);
      }
      return true;

    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

    //should not reach if success path works
    return false;
  }

 /*------------------------*/
 /* process methods */
 /*------------------------*/
  /**
   * checks if a process is still alive or not. Using this function rather than
   * isalive, as java states: 'isAlive() may return true for a brief period
   * after destroyForcibly() is called'
   *
   * @param p: the Process to check against
   * @return boolean: false if terminated, true if still alive
   */
  private static boolean hasProcessTerminated(Process p) {
    try {
      p.exitValue();
      return true;
    } catch (IllegalThreadStateException ex) {
      return false;
    }
  }

  /**
   * method to output (on console) the values of outputs and errors for that
   * process. Will mostly be used for debugging, but will a different iteration
   * of this to output %'s to user.
   */
  private static void outputProcess(Process p) {
    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
    BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    try {
      String s = null;
      System.out.println("output:");
      while ((s = output.readLine()) != null) {
        System.out.println(s);
      }
      System.out.println("\nerrors:");
      while ((s = error.readLine()) != null) {
        System.out.println(s);
      }
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

 /*------------------------*/
 /* daemon methods */
 /*------------------------*/
  private static final Runnable daemon = new Runnable() {
    @Override
    public void run() {
      String command = "ipfs daemon";
      Process p = null;
      try {
        p = Runtime.getRuntime().exec(command);
        p.waitFor(); //will hold the the runnable here, as daemon will be listening
        int exitStatus = p.exitValue(); //should not reach here
      } catch (IOException ex) {
        System.err.println("IPFS -> Runnable daemon: could not run command \"" + command + "\"");
      } catch (InterruptedException ex) {
        //forced interruption has occured, meaning that we want to kill the daemon
        System.err.println("Daemon inturrupted, so now ending");
        if (p != null) {
          p.destroy();
        }
        return; //if possible return early 
      }
    }
  };
  
  private static Thread d = null;
  
  /**
   * will start the IPFS daemon
   */
  public static void startDaemon() {
    d = new Thread(daemon);
    d.start();
  }

  /**
   * will stop the IPFS daemon 
   */
  public static void stopDaemon() {
    if (d != null) {
      d.interrupt(); //force interrupt on daemon to end it
      d = null; //set back to null
    }
  }
  
  // main method for quick testing
  public static void main(String[] args) {
    //scanner for input
    Scanner scan = new Scanner(System.in);

    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select file to add");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    String hash = null;
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      hash = IPFS.add(file);
      System.out.println("result Hash: " + hash);

    } else {
      System.out.println("Add cancelled");
    }

    chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select file to add new folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (hash != null) {
        System.out.println("Getting folder with IPFS hash: " + hash);
        System.out.println("New name of folder?");
        String newDirName = scan.nextLine().trim();
        if (newDirName.equals("")) {
          IPFS.get(file, hash);
        } else {
          IPFS.get(file, hash, newDirName);
        }
      }
    } else {
      System.out.println("Get cancelled");
    }
  }
}
