/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to call IPFS commands
 *
 * @author Prithpal Sooriya
 *
 * public functions:
 *
 * isInit() -> will check if IPFS node is initialised on host
 *
 * init() -> will initialise IPFs node on host add() -> recursive add a folder
 * to ipfs.
 *
 * get() -> get folder from ipfs (maybe want to have ipfsInstall(), but not too
 * sure how to implement this...)
 *
 * startDaemon -> start IPFS daemon
 *
 * stopDaemon -> stop IPFs daemon
 *
 *
 * private functions
 *
 * runCommand() -> will create a process to run a command
 *
 * hasProcessTerminated() -> checks if process has terminated or not
 *
 * outputProcess() -> outputs contents of a process
 */
public class IPFS {

  private static void runCommand(String command) {
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      //outputProcess(p);
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * isInit() checks if an IPFS node has been initialised for the host computer
   *
   * @return boolean true is IPFS node has been initialised, false if it hasn't.
   */
  public static boolean isInit() {
    String IPFSDir = System.getProperty("user.home") + "/.ipfs";
    File IPFSNode = new File(IPFSDir);
    return IPFSNode.exists();
  }

  /**
   * init() initialises an IPFS node for the host
   *
   * @return boolean true when init is complete
   *
   * NOTE: maybe make this void?
   */
  public static boolean init() {
    String command = "ipfs init";
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      return true;
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }
    //should not return false
    return false;
  }

  /**
   * add() used to add a FOLDER to ipfs
   *
   * @param file the java File representation for the folder to add
   * @param allowHidden add hidden(dot) files or not. will be used to add ipvc
   * @return String of the root hash, will return null if there was an
   * error
   */
  public static String add(File file, boolean allowHidden) {
    if (file == null) {
      System.err.println("IPFS - add(): folder given was null");
      return null;
    }
    if (!file.exists()) {
      System.err.println("IPFS - add(): folder doesn't exit");
      return null;
    }

    //ipfs add reecursive command. -r is recursive, -p, -Q is last hash only
    //by default, recursive add ignores DOT FOLDERS AND FILES (.folder or .file.txt)
    String command = "ipfs add -r -Q "
      + (allowHidden ? "-H " : " ")
      + '"' + file.getAbsolutePath() + '"';
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();

      String lastLine = "";
      String currentLine;
      BufferedReader output = new BufferedReader(
        new InputStreamReader(p.getInputStream()));
      while ((currentLine = output.readLine()) != null) {
        lastLine = currentLine;
      }
      return lastLine; //hash
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.err.println("IPFS - add(): should not have reached this part of function");
    return null;
  }

  /**
   * get() get a folder/file from IPFS
   *
   * @param destinationPath destination to add file/folder
   * @param hash hash to find in the IPFS network
   * @return boolean, true if file added to location, false if file was not
   * added (or error)
   */
  public static boolean get(File destinationPath, String hash) {
    return get(destinationPath, hash, null);
  }

  /**
   * get() get a folder/file from ipfs and store it in a with a new name
   *
   * @param destinationPAth destination to add file/folder
   * @param hash hash to find in the IPFS network
   * @param newDirName new name to give file/folder
   * @return boolean, true if added, false if not (or if error)
   */
  public static boolean get(File destinationPath, String hash, String newDirName) {
    if (destinationPath == null) {
      System.err.println("IPFS - get(): destinationPath is null");
      return false;
    }
    if (!destinationPath.exists()) {
      System.err.println("IPFS - get: destinationPath does not exist");
      return false;
    }
    if (!destinationPath.isDirectory()) {
      System.err.println("IPFS - get: destinationPath is not directory");
      return false;
    }

    String command = "ipfs get " + hash;
    try {
      Process p = Runtime.getRuntime().exec(command, null, destinationPath);
      p.waitFor();

      //now that file has been created, we can choose to rename it or not.
      if (newDirName != null) {
        String path = destinationPath.getAbsolutePath() + "/" + hash;
        File file = new File(path);
        File newFile = new File(destinationPath.getAbsolutePath() + "/" + newDirName);
        file.renameTo(newFile);
      }

      return true;
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.err.println("IPFS - get(): should not have reached this location in function");
    return false;
  }

  /**
   * hashProcessTerminated method to check if a process has been terminated or
   * not
   *
   * @param p processes to check if terminated
   * @return boolean true if terminated, false if not
   *
   * NOTE: this may not be necessary, but no harm in keeping this for future
   * use.
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
   * outputProcess() outputs the contents of a process (finished or not)
   *
   * @param p process to get content (outputs, errors from)
   *
   * NOTE: this method will only output to console, so mainly used for
   * debugging. NOTE: may be work making a different iteration of this method to
   * return string (e.g. % complete of add or get ipfs commands)?
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

  //Daemon methods
  private static Thread d = null;
  private static final Runnable daemon = new Runnable() {
    @Override
    public void run() {
      String command = "ipfs daemon --enable-namesys-pubsub";
      Process p = null;
      try {
        p = Runtime.getRuntime().exec(command);
        p.waitFor();
        int exitStatus = p.exitValue();
      } catch (IOException ex) {
        //should not reach io exception
        Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        //this can forcibly occure so we can end this runnable
//        System.err.println("Daemon force interrupted, so now ending");
        if (p != null) {
          p.destroy();
        }
        return; //if possible return early (may or maynot be significant)
      }
    }
  };

  /**
   * startDaemon() will start the IPFS daemon
   */
  public static void startDaemon() {
    d = new Thread(daemon);
    d.start();
  }

  /**
   * stopDaemon() will stop the IPFS daemon
   */
  public static void stopDaemon() {
    if (d != null) {
      d.interrupt();
      d = null; //becomes null again so we can reuse it (without it storing unneccesary memory when in use)
    }
  }

}
