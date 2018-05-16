/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev2.CMD;

import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class Start {

  public static void main(String[] args) {

    //run daemon
    IPFS.startIPFSDaemon();
    System.out.println("Running Daemon ");

    //scanner for user input
    Scanner scan = new Scanner(System.in);

    //JFileChooser to select files and folders
    JFileChooser chooser = new JFileChooser();

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
          //end daemon
          IPFS.stopIPFSDaemon();
          System.exit(0);
          break;
          
        case "add":
          
          break;
          
        default:
          System.out.println("Command not recognised");
      } //end switch
    } //end while
  } //end main method

}
