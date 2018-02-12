/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JFileChooser;

/**
 *
 * @author Prithpal Sooriya
 */
public class ABC {

  /*
  I want to test file system on windows and shit.
   */
  public static void main(String[] args) throws IOException {
    test2();
  }

  private static void test1() throws IOException {

    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    /*
      Using the IPFS-Java-API tests on github
      - specifically the directory test
        - to test adding a directory
     */
    Random rnd = new Random();
    String dirName = "folder" + rnd.nextInt(100);
    Path tmpDir = Files.createTempDirectory(dirName);

    String fileName = "afile" + rnd.nextInt(100);
    Path file = tmpDir.resolve(fileName);
    FileOutputStream fout = new FileOutputStream(file.toFile());
    byte[] fileContents = "IPFS rocks!".getBytes();
    fout.write(fileContents);
    fout.flush();
    fout.close();

    String subdirName = "subdir";
    tmpDir.resolve(subdirName).toFile().mkdir();

    String subfileName = "subdirfile" + rnd.nextInt(100);
    Path subdirfile = tmpDir.resolve(subdirName + "/" + subfileName);
    FileOutputStream fout2 = new FileOutputStream(subdirfile.toFile());
    byte[] file2Contents = "IPFS still rocks!".getBytes();
    fout2.write(file2Contents);
    fout2.flush();
    fout2.close();

    List<MerkleNode> addParts = ipfs.add(new NamedStreamable.FileWrapper(tmpDir.toFile()));
    MerkleNode addResult = addParts.get(addParts.size() - 1);

    //add the dir
    //note there is a NamedStreamable.DirWrapper
    System.out.println("localhost:8080/ipfs/" + addResult.hash);

    /*
    Outcome
    Seems that different file systems isnt the root problem...
    Adding directories does not allow it to be viewable on browser,
      so must be an issue with the code used?
    I can obtain the contents back through IPFS get via hash...
    Command line version works, but this code doesnt work??
    
    next test:
    try using NamedStreamable.DirWrapper
      - maybe I am using the wrong named streamable.
    */
  }
  
  private static void test2() throws IOException {
    
    /*
    This will test adding a dir based on the information from:
    IPFS-Java-API NamedStreamable.DirWrapper class https://github.com/ipfs/java-ipfs-api/blob/8e173b71973b8906488e4ded4ee730aa3546da59/src/main/java/io/ipfs/api/NamedStreamable.java
    This beautiful person (as well as others)
    */
    
    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
    
    
    Random rnd = new Random();
    String dirName = "folder" + rnd.nextInt(100);
    Path tmpDir = Files.createTempDirectory(dirName);

    String fileName = "afile" + rnd.nextInt(100);
    Path file = tmpDir.resolve(fileName);
    FileOutputStream fout = new FileOutputStream(file.toFile());
    byte[] fileContents = "IPFS rocks!".getBytes();
    fout.write(fileContents);
    fout.flush();
    fout.close();

    String subdirName = "subdir";
    tmpDir.resolve(subdirName).toFile().mkdir();

    String subfileName = "subdirfile" + rnd.nextInt(100);
    Path subdirfile = tmpDir.resolve(subdirName + "/" + subfileName);
    FileOutputStream fout2 = new FileOutputStream(subdirfile.toFile());
    byte[] file2Contents = "IPFS still rocks!".getBytes();
    fout2.write(file2Contents);
    fout2.flush();
    fout2.close();
    
    NamedStreamable.DirWrapper dir = new NamedStreamable.DirWrapper(dirName, Arrays.<NamedStreamable>asList());
    List<MerkleNode> nodes = ipfs.add(dir);
    System.out.println(nodes.size()); //returns size 1 (meaning only 1 item returned.. good.)
    MerkleNode root = nodes.get(nodes.size()-1);
    System.out.println("localhost:8080/ipfs/" + root.hash);
    
  }
}
