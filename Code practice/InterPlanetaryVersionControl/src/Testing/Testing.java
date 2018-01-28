/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Prithpal Sooriya
 */
public class Testing {

  public static void main(String[] args) {

    //use this area for testing functionality
    //for the version control system
    /* create accessor/node to access ipfs */
    //NEED TO RUN IPFS DAEMON (run via command line: "ipfs daemon")
    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    /* Add a file */
    //can be done via NamedStreamable.FileWrapper(File file) -> files
    //NamedStreamable.ByteWrapper(byte[]) -> bytes
    //added files are always pinned
    //using file chooser (for now)
    JFileChooser chooser = new JFileChooser();

    chooser.setCurrentDirectory(new File("."));
    chooser.setDialogTitle("Select file to host");
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    //filter different types of files to add
    //FileNameExtensionFilter filter = new FileNameExtensionFilter("txt & HTML ", "txt", "html");
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();

      //add file to ipfs and store version/s
      /*@EDIT better to use JSON format!! */
      try {
        List<MerkleNode> nodes;
        if (file.isDirectory()) {
          System.out.println("directory");
          System.out.println(file.getAbsolutePath());
          System.out.println(file.getName());
          nodes = ipfs.add(new NamedStreamable.FileWrapper(file));
          for (MerkleNode node : nodes) {
            System.out.println(node.toJSONString());
          }
          MerkleNode addResult = nodes.get(nodes.size()-1);
        } else {
          //get merkle node/s of the file (multiple nodes for a large file)
          nodes = ipfs.add(new NamedStreamable.FileWrapper(file));

//        //output the hashes to a file
//        File versionedFile = new File(file.getParent() + "/" + file.getName() + ".ipfs"); //add extension to file (hope it works)
//        if(!versionedFile.exists()){
//          versionedFile.createNewFile();
//          System.out.println("Created new file in: " + versionedFile.getAbsolutePath());
//        }
          //new line for storing new version merkel nodes
//        BufferedWriter writer = new BufferedWriter(new FileWriter(versionedFile, true)); //ensure to append to file
          for (MerkleNode node : nodes) {
//          writer.append(node.hash.toString() + " "); //hashes seperated by spaces..
            System.out.println(node.toJSONString()); //testing what output is!
          }
//        writer.append("\n"); //new line for end of version
//        writer.close();
        }
      } catch (IOException ex) {
        System.err.println("Issue when adding file");
      }
    }

  }

}
