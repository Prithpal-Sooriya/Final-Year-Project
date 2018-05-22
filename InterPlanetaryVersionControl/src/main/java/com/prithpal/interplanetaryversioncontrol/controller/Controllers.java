/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.controller;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class Controllers {
  
  
  public static File fileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we only want directories only!!
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    } else {
      System.out.println("choose directory cancelled");
    }
    return null;
  }
  
}
