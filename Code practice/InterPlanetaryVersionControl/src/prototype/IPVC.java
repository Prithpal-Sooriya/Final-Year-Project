/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPVC {

  //only to be used for command line version, will take out later
  Scanner scan = new Scanner(System.in);

  IPFS ipfs;

  //constructor
  public IPVC() {
    //create accessor/gateway for daemon and access commands
    ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001"); //access webgui of daemon
  }

  //version only used by command line, allows user to enter a commit message
  public void addFile(File file) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter commit message:");
    String commitMessage = scan.nextLine();

    addFile(file, commitMessage, author);
  }

  //maybe allow multiple commit messages?
  public void addFile(File file, String commitMessage, String author) {
    //get directory of the file
    String directory = file.getParent() + "/";
    //get filename without extension
    // --> will break if input is directory (need a better fix)
    // --> maybe is directory
    String fileName = file.getName();
    fileName = fileName.substring(0, fileName.lastIndexOf("."));

    File versionFile = new File(directory + fileName + ".ipvc");
    if (!versionFile.exists()) {
      try {
        versionFile.createNewFile();
        createJSONFileInformation(file);
      } catch (IOException ex) {
        System.err.println("Error in creating file");
        ex.printStackTrace();
      }
    }

    //add file to ipfs and update versions
    try {
      List<MerkleNode> nodes = ipfs.add(new NamedStreamable.FileWrapper(file));
      
      String versionJSON = createJSONVersion(nodes, commitMessage, author);
      MerkleNode node = nodes.get(nodes.size()-1); //last one is root node!
      
      //read in the json info
      //TIDY UP THIS
      FileReader reader = new FileReader(file);
      JSONParser parser = new JSONParser();
      try {
        JSONObject update = (JSONObject) parser.parse(reader);
        update.put("head", node.toJSONString()); //update head
        JSONArray versions = (JSONArray) update.get("versions");
        versions.add(versionJSON);
        update.put("versions", versions); //update versions array
        
        FileWriter fw = new FileWriter(file);
        fw.write(update.toJSONString()); //write the new json string --> computationally expensive... must be easier way to change file info...
        fw.flush();
        fw.close();
      } catch (ParseException ex) {
        Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
      }
      
    } catch (IOException ex) {
      System.err.println("Error in ipfs add file");
      ex.printStackTrace();
    }
  }

  private void createJSONFileInformation(File file) throws IOException {
    JSONObject header = new JSONObject();
    header.put("head", "");
    JSONArray versions = new JSONArray();
    header.put("versions", versions);
    
    FileWriter fw = new FileWriter(file, true);
    fw.append(header.toJSONString());
    fw.flush();
    fw.close();
  }
  
  private String createJSONVersion(List<MerkleNode> nodes, String commitMessage, String author) {
    JSONObject versionObj = new JSONObject();
    versionObj.put("Author", author);
    versionObj.put("Commit Message", commitMessage);
    versionObj.put("Date", new Date().toString());
    //array of json strings for all nodes
    JSONArray nodeObj =new JSONArray();
    for (MerkleNode node : nodes) {
      nodeObj.add(node.toJSONString());
    }
    versionObj.put("nodes", nodeObj);
    
    return versionObj.toJSONString();
  }

}
