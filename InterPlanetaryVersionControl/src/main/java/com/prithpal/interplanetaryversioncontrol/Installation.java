/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Prithpal
 */
public class Installation {
  
  public Installation(String installationDir) throws Exception {
    File dir = new File(installationDir);
    
    if(!dir.exists() || !dir.isFile()){
      throw new Exception("The installation directory: " + dir.getCanonicalPath()
              + "does not exist, thus cannot be installed");
    }
    
    File ipvcFile = new File(OSUtilities.getApplicationName());
    File ipvc_cliFile = new File(OSUtilities.getApplicationCLIName());
    
    if(!ipvcFile.exists() || !ipvc_cliFile.exists()) {
      
    }
    
    
    
  }
  
  
}
