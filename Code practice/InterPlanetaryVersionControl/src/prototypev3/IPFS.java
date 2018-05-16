/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 * Static class for calling some IPFS functions
 *
 * @author Prithpal Sooriya
 */
public class IPFS {

  /**
   * checks if there is a .ipfs directory for the user
   *
   * @return boolean: true if it does exist, false if it doesnt exist
   */
  public static boolean isInit() {
    String IPFSDir = System.getProperty("user.home") + "/.ipfs";
    File IPFSFolder = new File(IPFSDir);
    return IPFSFolder.exists();
  }

  /**
   * performs ipfs init
   */
  public static void IPFSInit() {
    String command = "ipfs init";
    Process p;
    try {
      p = Runtime.getRuntime().exec(command);
      p.waitFor();

      //read outputs
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

      String s = null;
      System.out.println("output:");
      while ((s = input.readLine()) != null) {
        System.out.println(s);
      }

      System.out.println("");
      System.out.println("errors:");
      while ((s = error.readLine()) != null) {
        System.out.println(s);
      }

    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  public static void IPFSAddTest() {

    String pathToFile = null;

    //for testing purposes, I will use jfile chooser here
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select file to add");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      pathToFile = file.getAbsolutePath();
    } else {
      System.out.println("Add cancelled");
    }

    //string needs to be formatted (because spaces in path can cause issues)
//    pathToFile = pathToFile.replaceAll(" ", System.getProperty("file.separator"));
    System.out.println(pathToFile);

    if (pathToFile != null) {
      //first need to go to file
      String command = "ipfs add -r \"" + pathToFile + "\"";
      try {
        Process p = Runtime.getRuntime().exec(command);

        //output to user %done over time, rather than being stuck in waitfor();
        while (!hasProcessTerminated(p)) {
          //read outputs
//          BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
          BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
          String s = null;
          while ((s = error.readLine()) != null) {
            System.out.println(s);
          }
          Thread.sleep(100);
        }

        //read outputs
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String s = null;
        System.out.println("output:");
        while ((s = input.readLine()) != null) {
          System.out.println(s);
        }

      } catch (IOException ex) {
        Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (InterruptedException ex) {
        Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static void IPFSGetTest(String hash) {
    //for testing purposes, we select location here
    File fileDir = null;

    //for testing purposes, I will use jfile chooser here
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select folder to add");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      fileDir = chooser.getSelectedFile();
    } else {
      System.out.println("Add cancelled");
    }
    
    if(fileDir != null) {
      try {
        //go to directory
//        Process p = null;
//        System.out.println(pathToFile);
//        p = Runtime.getRuntime().exec("cd " + pathToFile);
//        p.waitFor();
        
        //call ipfs get
        //3 param runtime.exec(STRING command, STRING environment variables[], FILE starting directory)
        Process p = Runtime.getRuntime().exec("ipfs get " + hash, null, fileDir);
        while(!hasProcessTerminated(p)) {
          BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
          String s = null;
          while ((s = error.readLine()) != null) {
            System.out.println(s);
          }
          Thread.sleep(100); //do not want constant calling!
        }
        
        //read outputs
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        System.out.println("output:");
        while ((s = input.readLine()) != null) {
          System.out.println(s);
        }
        
      } catch (IOException ex) {
        Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
  private static boolean hasProcessTerminated(Process p) {
    try {
      p.exitValue();
      return true;
    } catch (IllegalThreadStateException ex) {
      return false;
    }
  }

  /* Daemon controls */
 /*--------------------------------------------------------------------------*/
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

  public static void startDaemon() {
    d = new Thread(daemon);
    d.start();
  }

  public static void stopDaemon() {
    if (d != null) {
      d.interrupt(); //force interrupt on daemon to end it
      d = null; //set back to null
    }
  }

  public static boolean isDaemonRunning() {
    return d.isAlive();
  }
  
  public static void swarmPeers() {
    try {
      String command = "ipfs swarm peers";
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      //read outputs
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

      String s = null;
      System.out.println("output:");
      while ((s = input.readLine()) != null) {
        System.out.println(s);
      }

      System.out.println("");
      System.out.println("errors:");
      while ((s = error.readLine()) != null) {
        System.out.println(s);
      }
    } catch (IOException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  //main method for testing
  public static void main(String[] args) throws InterruptedException {
//    IPFS.startDaemon();
////    Thread.sleep(5000);
//    IPFS.IPFSAddTest();
////    IPFS.IPFSGetTest("QmY15KGenHzs2e3y2Y9wsukYw9xgzrUZjMbheaZabxfUtt");
//    IPFS.IPFSGetTest("QmRQoL5cgB5rRSLWRd3Swqq1GTXsp5idwH3NFe4NiXCUuj");
//    IPFS.stopDaemon();
//      IPFSInit();
  }
}
