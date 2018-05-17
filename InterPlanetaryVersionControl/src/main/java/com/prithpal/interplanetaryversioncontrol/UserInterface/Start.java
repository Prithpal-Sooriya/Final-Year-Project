/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.UserInterface;

import com.prithpal.interplanetaryversioncontrol.Logger;
import com.prithpal.interplanetaryversioncontrol.OSUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import javax.swing.UIManager;

/**
 *
 * @author Prithpal
 */
public class Start {

  public static void main(String args[]) throws IOException {
    //TODO: support for other operating systems
    try {
      OSUtilities.OS_TYPE os = OSUtilities.getOSType();
      if (os == OSUtilities.OS_TYPE.WINDOWS) {
        createConfigFile();
      }

      Logger.info("Starting Application");
      Logger.info("OS: " + System.getProperty("os") + " = " + os);
      Logger.info("Current Directory: " + new File(".").getCanonicalPath());
      Logger.info("Class Path: " + System.getProperty("java.class.path")); //path for java built jar's and dependency jar's
      Logger.info("Environment PATH: " + System.getenv("PATH")); //all environment variables in path
      
      //TODO: support other OS's
      if(os == OSUtilities.OS_TYPE.WINDOWS) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      
      //start up the jframe
      
      
    } catch (Exception ex) {

    }
  }

  private static void createConfigFile() throws IOException {
    File fDir = new File(OSUtilities.getSettingsDirectory());
    if (!fDir.exists()) {
      if (!fDir.mkdirs()) {
        Logger.error("Could not create settings directory: " + fDir.getCanonicalPath());
        throw new IOException("Could not create settings directory: " + fDir.getCanonicalPath());
      }
    }

    File config = new File(fDir, "IPVC.conf");
    if (!config.exists()) {
      Logger.info(
              "IPVC config file " + config.getCanonicalPath()
              + " does not exist. Creating config file with default settings."
      );
      
      try (PrintStream out = new PrintStream(new FileOutputStream(config))) {
        out.println("##############################################################################");
        out.println("#             Inter Planetary Version Control Configuration File             #");
        out.println("##############################################################################");
        out.println("# This config file contains important information used by this application   #");
        out.println("# Creation Date: " + new Date().toString());
        out.println("##############################################################################");
      }
    }
  }
}
