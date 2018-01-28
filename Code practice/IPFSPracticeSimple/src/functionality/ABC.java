/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class ABC {
  public static void main(String[] args) throws IOException {
    JFileChooser chooser = new JFileChooser();

    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select file to host");
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      
      System.out.println(file.getParent());
      
      File f = new File (file.getParent() + "/versioning.txt");
      if(!f.exists()) {
        f.createNewFile();
      }
      
      BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
      bw.append("Hello2\n");
      bw.close();
            
      
    }
    
  }
}
