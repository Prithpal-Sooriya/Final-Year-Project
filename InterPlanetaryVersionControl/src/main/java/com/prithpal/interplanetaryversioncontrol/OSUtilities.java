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

  public enum OS_TYPE {
    LINUX,
    WINDOWS,
    MAC_OS,
    OTHER_UNIX,
    OTHER_OS
  };

  public static boolean isUnix(OS_TYPE os) {
    return os.LINUX == OS_TYPE.LINUX
            || os.MAC_OS == OS_TYPE.MAC_OS
            || os == OS_TYPE.OTHER_UNIX;
  }

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
    String name = "InterPlanetaryVersionControl";
    OS_TYPE os = getOSType();
    if (os == OS_TYPE.WINDOWS) {
      name += ".exe";
    }
    return name;
  }

  public static String getApplicationToolName() {
    //TODO: change this name to something shorter!
    String name = "InterPlanetaryVersionControl-cli";
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
}
