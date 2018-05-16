/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev1.GUI;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

/**
 *
 * @author Prithpal Sooriya
 */

//this will be a class that will be used to allow the user to drag and drop files into the gui
//found information from this regarding Oracle and other websites
//e.g. https://stackoverflow.com/a/39415436
public class FileDropHandler extends TransferHandler {

  
  //override method to see if the file can be imported!
  @Override
  public boolean canImport(TransferSupport support) {
    for(DataFlavor flavor : support.getDataFlavors()) {
      //allows import of list of files objects
      if(flavor.isFlavorJavaFileListType()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean importData(TransferSupport support) {
    if(!this.canImport(support)) {
      return false; //not allowed to import!
    }
    
    List<File> files;
    try {
      files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
    } catch (UnsupportedFlavorException|IOException ex) {
      System.out.println("import data issue");
      ex.printStackTrace();
      return false;
    }
    
    //we have a list of files now!!
    //do stuff with file!
    for (File file : files) {
      //works!!! --> need to either handle files back to gui or to a controller
//      System.out.println(file.getAbsolutePath());
      String author = JOptionPane.showInputDialog("Enter Author");
      String commitMessage = JOptionPane.showInputDialog("Enter commitMessage");
      Controller.addFileToIPFS(file, commitMessage, author);
    }
    return true;
  }
  
  
  
}
