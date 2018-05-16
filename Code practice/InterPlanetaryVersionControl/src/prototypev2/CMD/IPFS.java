/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev2.CMD;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to handle IPFS Daemon and the daemon commands
 *
 * @author Prithpal Sooriya
 */
public class IPFS {
  

  private static final Runnable run = new Runnable() {
    @Override
    public void run() {
      String command = "ipfs daemon";
      Process p = null;
      try {
        p = Runtime.getRuntime().exec(command);
//        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//          @Override
//          public void run() {
//            p.destroy();
//          }
//        }));
        p.waitFor(); //daemon will keep running in background

        //once daemon ends return exitStatus
        //close 
        int exitStatus = p.exitValue();
      } catch (InterruptedException ex) {
//        //end ipfs!
//        char ctrlC = 0x3;
//        command = Character.toString(ctrlC);
//        try {
//          p = Runtime.getRuntime().exec(command);
//          p.waitFor();
//          System.out.println(p.exitValue());
//          p = Runtime.getRuntime().exec(command);
//          p.waitFor();
//          System.out.println(p.exitValue());
//          
//        } catch (IOException ex1) {
//          Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex1);
//        } catch (InterruptedException ex1) {
//          Logger.getLogger(IPFS.class.getName()).log(Level.SEVERE, null, ex1);
//        }
        if (p != null) {
          p.destroy();
        }
        return;
      } catch (IOException ex) {
        Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  };
  private static Thread d;

  /**
   * Static function to init the daemon
   *
   */
  public static void startIPFSDaemon() {
    d = new Thread(run);
    d.start();
  }

  public static void stopIPFSDaemon() {
    if (d != null) {
      d.interrupt();
      d = null;
    }
  }

}
