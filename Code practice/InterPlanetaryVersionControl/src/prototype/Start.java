/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import java.io.File;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class Start {

  /*
  simple command line prototype
   */
  public static void main(String[] args) {

    //scanner for input
    Scanner scan = new Scanner(System.in);
    
    //IPVC to access version control commands
    IPVC ipvc = new IPVC();

    System.out.println("Inter Planetary Version Control");
    System.out.println("===============================");
    System.out.println("type '--help' for commands");

    while (true) {
      String input = scan.next();
      switch (input) {
        case "--help":
          System.out.println("Command: 'add'");
          System.out.println(" - Function: Will open up a window to add a file onto ipfs, and will display the hash for that file.");
          System.out.println("             A versions (.ipvc) will be created in the same directory as the file added");
          System.out.println("");
          System.out.println("Command: 'versions'");
          System.out.println(" - Function: Will open up a window to search for versions file (.ipvc file) and display all versions");
          System.out.println("");
          System.out.println("Command: 'exit'");
          System.out.println(" - Function: will close this program");
          break;

        case "exit":
          //need a better exit condition
          System.exit(0);
          break;

        case "add":
          //search for file to add.
          JFileChooser chooser = new JFileChooser();
//          chooser.setCurrentDirectory(new File("."));
          chooser.setDialogTitle("Select file to add");
          int returnVal = chooser.showOpenDialog(null);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            
            ipvc.addFile(file);
          }
          else {
            System.out.println("Add cancelled");
          }
          break;

        case "versions":
          //search for versions file
          break;

        default:
          System.out.println("Input Not Recognised");
      }

    }

  }

}
