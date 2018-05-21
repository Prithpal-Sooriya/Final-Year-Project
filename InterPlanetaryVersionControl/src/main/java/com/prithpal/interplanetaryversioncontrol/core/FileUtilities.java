/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.core;

import com.prithpal.interplanetaryversioncontrol.OSUtilities;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prithpal
 */
public class FileUtilities {
  
  public static void writeFile(File f, String str) throws IOException {
    try (FileWriter fw = new FileWriter(f)) {
      fw.write(str);
      fw.flush();
    }
  }
  
  public static void writeFileLarge(File f, List<String> strings) throws IOException {
      try (FileWriter fw = new FileWriter(f)) {
        strings.forEach(string -> {
          try {
            fw.append(string);
          } catch (IOException ex) {
          }
        });
        fw.flush();
      }
  }
  
  public static String readFile(File f) throws IOException {
    StringBuffer sb = new StringBuffer();
    Files.readAllLines(f.toPath()).forEach(line -> sb.append(line));
    return sb.toString();
    
  }
  
  public static String getSettingsDirectory() throws IOException {
     File fDir = new File(OSUtilities.getSettingsDirectory());
    if (!fDir.exists()) {
      if (!fDir.mkdirs()) {
        com.prithpal.interplanetaryversioncontrol.Logger.error("Could not create settings directory: " + fDir.getCanonicalPath());
        throw new IOException("Could not create settings directory: " + fDir.getCanonicalPath());
      }
    }
    
    return fDir.getCanonicalPath();
  }
  
  
}
