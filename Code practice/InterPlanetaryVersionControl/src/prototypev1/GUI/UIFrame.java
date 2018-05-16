/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev1.GUI;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 *
 * @author Prithpal Sooriya
 */
public class UIFrame extends javax.swing.JFrame {

  /**
   * Creates new form UIFrame
   */
  public UIFrame() {
    initComponents();

    //will be adding the drag and drop functionality
    dragdropPanel.setTransferHandler(new FileDropHandler());

    //listener for the qrcode click
    qrLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        //only activate if we have a hash
        if (!currentQrHash.isEmpty()) {
          String website = "http://localhost:8080/ipfs/" + currentQrHash; //best to use this on desktop as it is faster/fastest
          try {
            Desktop.getDesktop().browse(new URI(website));
          } catch (URISyntaxException | IOException ex) {
            System.out.println("Error in uri");
            ex.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    leftFrame = new javax.swing.JPanel();
    dragdropPanel = new javax.swing.JPanel();
    dragdropLabel = new javax.swing.JLabel();
    rightFrame = new javax.swing.JPanel();
    jSeparator1 = new javax.swing.JSeparator();
    qrLabel = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setMaximumSize(new java.awt.Dimension(600, 300));
    setMinimumSize(new java.awt.Dimension(600, 300));
    getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    leftFrame.setBackground(new java.awt.Color(52, 73, 94));
    leftFrame.setAlignmentX(0.0F);
    leftFrame.setAlignmentY(0.0F);
    leftFrame.setMaximumSize(new java.awt.Dimension(300, 300));
    leftFrame.setMinimumSize(new java.awt.Dimension(300, 300));
    leftFrame.setPreferredSize(new java.awt.Dimension(298, 300));

    dragdropPanel.setBackground(new java.awt.Color(52, 73, 94));
    dragdropPanel.setMaximumSize(new java.awt.Dimension(300, 300));
    //dashed boarder
    dragdropPanel.setBorder(BorderFactory.createDashedBorder
      (new Color(45, 52, 54), 3, 10, 5, true));

    dragdropLabel.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
    dragdropLabel.setForeground(java.awt.Color.darkGray);
    dragdropLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    dragdropLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/IPFS image.png"))); // NOI18N
    dragdropLabel.setLabelFor(dragdropPanel);
    dragdropLabel.setText("Drag and drop an image");
    dragdropLabel.setAlignmentY(0.0F);
    dragdropLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

    javax.swing.GroupLayout dragdropPanelLayout = new javax.swing.GroupLayout(dragdropPanel);
    dragdropPanel.setLayout(dragdropPanelLayout);
    dragdropPanelLayout.setHorizontalGroup(
      dragdropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(dragdropPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(dragdropLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        .addContainerGap())
    );
    dragdropPanelLayout.setVerticalGroup(
      dragdropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(dragdropPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(dragdropLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        .addContainerGap())
    );

    javax.swing.GroupLayout leftFrameLayout = new javax.swing.GroupLayout(leftFrame);
    leftFrame.setLayout(leftFrameLayout);
    leftFrameLayout.setHorizontalGroup(
      leftFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(leftFrameLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(dragdropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    leftFrameLayout.setVerticalGroup(
      leftFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(leftFrameLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(dragdropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    getContentPane().add(leftFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 298, 300));

    rightFrame.setBackground(new java.awt.Color(52, 73, 94));
    rightFrame.setAlignmentX(0.0F);
    rightFrame.setAlignmentY(0.0F);
    rightFrame.setMaximumSize(new java.awt.Dimension(302, 300));
    rightFrame.setMinimumSize(new java.awt.Dimension(302, 300));
    rightFrame.setPreferredSize(new java.awt.Dimension(302, 300));

    jSeparator1.setBackground(new java.awt.Color(45, 45, 45));
    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator1.setToolTipText("");
    jSeparator1.setAlignmentX(0.0F);
    jSeparator1.setAlignmentY(0.0F);
    jSeparator1.setPreferredSize(new java.awt.Dimension(2, 280));
    jSeparator1.setRequestFocusEnabled(false);
    jSeparator1.setVerifyInputWhenFocusTarget(false);

    qrLabel.setMaximumSize(new java.awt.Dimension(222, 290));

    javax.swing.GroupLayout rightFrameLayout = new javax.swing.GroupLayout(rightFrame);
    rightFrame.setLayout(rightFrameLayout);
    rightFrameLayout.setHorizontalGroup(
      rightFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(rightFrameLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(qrLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        .addContainerGap())
    );
    rightFrameLayout.setVerticalGroup(
      rightFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
      .addGroup(rightFrameLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(qrLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    getContentPane().add(rightFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, 0, 310, 300));

    pack();
  }// </editor-fold>//GEN-END:initComponents

  /**
   * @param args the command line arguments
   */
//  public static void main(String args[]) {
//    /* Set the Nimbus look and feel */
//    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//     */
//    try {
//      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//        if ("Nimbus".equals(info.getName())) {
//          javax.swing.UIManager.setLookAndFeel(info.getClassName());
//          break;
//        }
//      }
//    } catch (ClassNotFoundException ex) {
//      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (InstantiationException ex) {
//      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (IllegalAccessException ex) {
//      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//      java.util.logging.Logger.getLogger(UIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    }
//    //</editor-fold>
//
//    /* Create and display the form */
//    java.awt.EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        JFrame frame = new UIFrame();
//        frame.setVisible(true);
//        frame.setLocationRelativeTo(null);
//        UIDragListener dragUI = new UIDragListener(frame);
//        frame.addMouseListener(dragUI);
//        frame.addMouseMotionListener(dragUI);
//      }
//    });
//  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel dragdropLabel;
  private javax.swing.JPanel dragdropPanel;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPanel leftFrame;
  private javax.swing.JLabel qrLabel;
  private javax.swing.JPanel rightFrame;
  // End of variables declaration//GEN-END:variables

  //method to allow the jpanel to showqrcode
  private String currentQrHash;

  public void changeQRCode(String contentHash) {
    currentQrHash = contentHash; //so we can use later
    String website = "ipfs.io/ipfs/" + contentHash;
    File file = QRCode.from(website).to(ImageType.JPG).file();

    try {
      BufferedImage image = ImageIO.read(file);
      Image scaledImage = image.getScaledInstance(qrLabel.getWidth(), qrLabel.getHeight(), Image.SCALE_SMOOTH);
      qrLabel.setIcon(new ImageIcon(scaledImage));
      qrLabel.setHorizontalAlignment(JLabel.CENTER);
      qrLabel.setVerticalAlignment(JLabel.CENTER);
    } catch (IOException ex) {
      System.out.println("IO Exception");
      ex.printStackTrace();
    }
  }

  //to allow the dragging of the frame
  //code idea obtained from https://stackoverflow.com/questions/16046824/making-a-java-swing-frame-movable-and-setundecorated
  public static class UIDragListener extends MouseAdapter {

    private final JFrame frame;
    private Point mouse = null;

    public UIDragListener(JFrame frame) {
      this.frame = frame;
    }

    public void mouseReleased(MouseEvent e) {
      mouse = null;
    }

    public void mousePressed(MouseEvent e) {
      mouse = e.getPoint();
    }

    public void mouseDragged(MouseEvent e) {
      Point currCoords = e.getLocationOnScreen();
      frame.setLocation(currCoords.x - mouse.x, currCoords.y - mouse.y);
    }
  }

}
