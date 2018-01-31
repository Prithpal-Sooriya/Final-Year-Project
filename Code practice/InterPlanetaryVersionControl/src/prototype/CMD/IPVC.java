/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype.CMD;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import java.io.File;
import java.io.FileNotFoundException;
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
  //static constants for the json keys
  private static final String JSONOBJECT_HEAD_KEY = "head"; //will return a JSONObject for the head
  private static final String JSONOBJECT_VERSIONS_KEY = "versions"; //will return a JSONArray of all JSONObject versions
  private static final String JSONOBJECT_AUTHOR_KEY = "author"; //will return String for author name
  private static final String JSONOBJECT_COMMIT_KEY = "commitMessage"; //will return String for commit message
  private static final String JSONOBJECT_DATE_KEY = "date"; //will return String for the date
  private static final String JSONOBJECT_MERKLENODES_KEY = "nodes"; //will return a json array of MerkleNodes
  
  /*
  how the json is planned out (probs will need to talk to Ian about this, or look up more details)
  
  "head" will contain a JSONObject --> which will be a version (that contains: author, commit message, date, merklenodes)
  "versions" will return a JSONArray of versions (JSONObjects, with contents: author, commit message, date, merklenodes)
  */

  //only to be used for command line version, will take out later
  Scanner scan = new Scanner(System.in);

  IPFS ipfs;

  //constructor
  public IPVC() {
    //create accessor/gateway for daemon and access commands
    ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001"); //access webgui of daemon
  }

  //version only used by command line, allows user to enter a commit message
  //may move this outside of this class
  public void addFile(File file) {
    System.out.println("Enter author:");
    String author = scan.nextLine();
    System.out.println("Enter commit message:");
    String commitMessage = scan.nextLine();

    addFile(file, commitMessage, author);
  }

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
        createJSONFileInformation(versionFile);
      } catch (IOException ex) {
        System.err.println("Error in creating file");
        ex.printStackTrace();
      }
    }

    //add file to ipfs and update versions
    try {
      List<MerkleNode> nodes = ipfs.add(new NamedStreamable.FileWrapper(file));
      
      JSONObject versionJSON = createJSONVersion(nodes, commitMessage, author);
      MerkleNode node = nodes.get(nodes.size()-1); //last one is root node!
      
      //read in the json info
      FileReader reader = new FileReader(versionFile);
      JSONParser parser = new JSONParser();
      try {
        
        JSONObject update = (JSONObject) parser.parse(reader);
        update.put(JSONOBJECT_HEAD_KEY, versionJSON); //update head --> will contain whole version, not just merkle root node
        JSONArray versions = (JSONArray) update.get(JSONOBJECT_VERSIONS_KEY);
        versions.add(versionJSON);
        update.put(JSONOBJECT_VERSIONS_KEY, versions); //update versions array
        
        FileWriter fw = new FileWriter(versionFile);
        fw.write(update.toJSONString()); //write the new json string --> computationally expensive... must be easier way to change file info...
        fw.flush();
        fw.close();
      } catch (ParseException ex) {
        System.err.println("Error when added json");
        ex.printStackTrace();
      }
      
      //notify the user
      System.out.println("File Added!");
      System.out.println("Access from:");
      System.out.println("IPFS/IPFS gateway:");
      System.out.println("localhost:8080/ipfs/"+node.hash.toString());
      System.out.println("HTTP/IPFS gateway:");
      System.out.println("gateway.ipfs.io/ipfs/"+node.hash.toString());
      System.out.println("ipfs.io/ipfs/"+node.hash.toString());
      
      
    } catch (IOException ex) {
      System.err.println("Error in ipfs add file");
      ex.printStackTrace();
    }
  }
  
  public void versions(File file) {
    try {
      //file is in json, so construct hashes from json
      FileReader reader = new FileReader(file);
      JSONParser parser = new JSONParser();
      try {
        JSONObject update = (JSONObject) parser.parse(reader);
        JSONArray versions = (JSONArray) update.get(JSONOBJECT_VERSIONS_KEY);

        for (Object version : versions) {
          JSONObject v = (JSONObject) version;
          System.out.println(v.get(JSONOBJECT_AUTHOR_KEY) + ", " + v.get(JSONOBJECT_COMMIT_KEY) + ", " + v.get(JSONOBJECT_DATE_KEY));
          JSONArray JSONNodes = (JSONArray) v.get(JSONOBJECT_MERKLENODES_KEY);
          for (Object JSONNode : JSONNodes) {
            JSONObject rawNode = (JSONObject) parser.parse((String)JSONNode);
            MerkleNode node = MerkleNode.fromJSON(rawNode);
            System.out.println(node.toJSONString());
          }
//          for (Object JSONNode : JSONNodes) {
//            System.out.println(((MerkleNode)JSONNode).toJSONString());
//          }
          System.out.println("===================");
        }
        
      } catch (ParseException|IOException ex) {
        System.err.println("Error when reading json");
        Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
      }
    } catch (FileNotFoundException ex) {
      System.out.println("Error when reading json file");
      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    }
    
  }

  private void createJSONFileInformation(File file) throws IOException {
    JSONObject header = new JSONObject();
    header.put(JSONOBJECT_HEAD_KEY, "");
    JSONArray versions = new JSONArray();
    header.put(JSONOBJECT_VERSIONS_KEY, versions);
    
    FileWriter fw = new FileWriter(file);
    fw.write(header.toJSONString());
    fw.flush();
    fw.close();
  }
  
  private JSONObject createJSONVersion(List<MerkleNode> nodes, String commitMessage, String author) {
    JSONObject versionObj = new JSONObject();
    versionObj.put(JSONOBJECT_AUTHOR_KEY, author);
    versionObj.put(JSONOBJECT_COMMIT_KEY, commitMessage);
    versionObj.put(JSONOBJECT_DATE_KEY, new Date().toString());
    //array of json strings for all nodes
    JSONArray nodeObj =new JSONArray();
    for (MerkleNode node : nodes) {
      nodeObj.add(node.toJSONString()); //EDIT use toJSONString rather than toJSON (because toJSON does not wrap "" on hashes)
    }
    versionObj.put(JSONOBJECT_MERKLENODES_KEY, nodeObj);
    return versionObj;
  }
  
  public String createJSONVersionString(List<MerkleNode> nodes, String commitMessage, String author) {
    return createJSONVersion(nodes, commitMessage, author).toJSONString();
  }

}
