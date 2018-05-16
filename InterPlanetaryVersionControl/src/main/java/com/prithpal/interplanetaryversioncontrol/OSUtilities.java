/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Handles the Operating System utilities
 *
 * @author Prithpal
 */
public class OSUtilities {

  /**
   * Constants and variables
   * APPLICATION_NAME = name of the application
   * @TODO the APPLICATION_NAME = "InterPlanetaryFileSystem" is too long, maybe a shorter name?
   * 
   * OS_TYPE = the types of OS supported
   * @TODO this class needs to implement some of the OS types to support.
   */
  private static final String APPLICATION_NAME = "InterPlanetaryVersionControl";
  
  public enum OS_TYPE {
    LINUX,
    WINDOWS,
    MAC_OS,
    OTHER_UNIX,
    OTHER_OS
  };

  
  /**
   * isUnix(OS_TYPE os)
   * @param os the type of operating system
   * @return boolean if the OS is UNIX
   */
  public static boolean isUnix(OS_TYPE os) {
    return os.LINUX == OS_TYPE.LINUX
            || os.MAC_OS == OS_TYPE.MAC_OS
            || os == OS_TYPE.OTHER_UNIX;
  }

  /**
   * getOSType()
   * @return OS_TYPE the type of OS this program is running on.
   */
  public static OS_TYPE getOSType() {
    String name = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    if (name.contains("linux")) {
      return OS_TYPE.LINUX;
    }
    if (name.contains("windows")) {
      return OS_TYPE.WINDOWS;
    }
    if (name.contains("darwin") || name.contains("mac os") || name.contains("macos")) {
      return OS_TYPE.MAC_OS;
    }
    if (name.contains("unix")) {
      return OS_TYPE.OTHER_UNIX;
    }
    return OS_TYPE.OTHER_OS; //unknown OS
  }

  public static String getApplicationName() {
    //TODO: change this name to something shorter!
    String name = APPLICATION_NAME;
    OS_TYPE os = getOSType();
    if (os == OS_TYPE.WINDOWS) {
      name += ".exe";
    }
    return name;
  }

  public static String getApplicationToolName() {
    //TODO: change this name to something shorter!
    String name = APPLICATION_NAME+"-cli";
    OS_TYPE os = getOSType();
    if (os == OS_TYPE.WINDOWS) {
      name += ".exe";
    }
    return name;
  }

  public static String getProgramDirectory() throws IOException {
    //TODO: search based on a file (to find the dir?), maybe 
    return null;
  }
  
  public static File getUserHomeDirectory() throws IOException{
    return new File(System.getProperty("user.home"));
  }
  
  public static String getSettingsDirectory() throws IOException {
    File userHome = getUserHomeDirectory();
    File dir;
    OS_TYPE os = getOSType();
    
    if (os == OS_TYPE.WINDOWS) {
      dir = new File(System.getenv("LOCALAPPDATA") + "\\" + APPLICATION_NAME);
    }
    else if (os == OS_TYPE.MAC_OS) {
      dir = new File(userHome, "Library/Application Support/"+APPLICATION_NAME);
    }
    else {
      dir = new File(userHome.getCanonicalPath(), File.separator + "." + APPLICATION_NAME);
    }
    
    if(!dir.exists()) {
      if(!dir.mkdirs()) {
        Logger.warning("could not create application settings directory: " + dir.getCanonicalPath());
      }
    }
    return dir.getCanonicalPath();
  }
  
  public static String getSystemInfo() throws IOException, InterruptedException {
    //must find a better way to gather system information
    return System.getProperty("os.name") + System.getProperty("os.version");
  }
  
  public static File getIPFSCommand(String command) throws IOException {
    //TODO support for multiple operating systems
    OS_TYPE os = getOSType();
    
    //TODO: find a cleaner way of accessing the IPFS command
    if(os == OS_TYPE.WINDOWS) {
      String progFilesString = System.getenv("PROGRAMFILES");
      if((progFilesString != null) && (!progFilesString.isEmpty())) {
        File progFilesFile = new File(progFilesString);
        if(isFolder(progFilesFile)) {
          File IPFS = new File(progFilesFile,"IPFS");
          if(isFolder(IPFS)) {
            File commandFile = new File(IPFS, command);
            if(commandFile.exists() && commandFile.isFile()) {
              return commandFile;
            }
          }
        }
      }
    }
    
    //TODO: find ipfs location on other operating systems
    //...
    return null;
  }
  
  private static boolean isFolder(File f) {
    return f.exists() && f.isDirectory();
  }
  
  public static String wrapString(String param) {
    //helpful notes found on various github sources --> windows can be difficult to deal with on command line
    OS_TYPE os = OSUtilities.getOSType();
    if(os == OS_TYPE.WINDOWS) {
      param = '"' + param.replace("\"", "\\\"") + '"';
    }
    return param;
  }
}
