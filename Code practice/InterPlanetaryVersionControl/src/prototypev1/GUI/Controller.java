/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev1.GUI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.swing.JFrame;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 *
 * @author Prithpal Sooriya
 */
public class Controller {
  
  private static UIFrame frame;
  
  private static IPVC ipvc = new IPVC();
  
  //this function will take in the file from handler, and return information to gui
  public static void addFileToIPFS(File file, String commitMessage, String author) {
    
//    String hash = ipvc.addFile(file, commitMessage, author);
    String hash = ipvc.addDir(file, commitMessage, author);
    frame.changeQRCode(hash);
    
  }
  
  
  
  //this place will be the main method
  //will run the view, and update the view
  //inputs from view will be handed to a listener, which will hand back to controller
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        frame = new UIFrame();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        UIFrame.UIDragListener dragUI = new UIFrame.UIDragListener(frame);
        frame.addMouseListener(dragUI);
        frame.addMouseMotionListener(dragUI);
      }
    });
  }
  
}
