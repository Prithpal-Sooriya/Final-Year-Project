/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.ipfspracticesimple;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Prithpal Sooriya
 */
public class IPFSPracticeSimple {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    System.out.println("Hello world!");

    /* 
        we will need to run the ipfs daemon,
        so we can use it as HTTP/IPFS gateway interface
     */
    //creates new ipfs node for the client
    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    try {
      //return all local references (in .ipfs folder)
      ipfs.refs.local();
//            for (Multihash multihash : ipfs.refs.local()) {
//                System.out.println(multihash);
//            }
    } catch (IOException ex) {
      Logger.getLogger(IPFSPracticeSimple.class.getName()).log(Level.SEVERE, null, ex);
    }

    //To add a file use -> will return a list of merkelnodes
    try {
      new File("./src/Testing/ipfspracticesimple/Hello.txt").createNewFile();
    } catch (IOException ex) {
      Logger.getLogger(IPFSPracticeSimple.class.getName()).log(Level.SEVERE, null, ex);
    }
    NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("./src/Testing/ipfspracticesimple/Hello.txt"));
    try {
      MerkleNode addResult = ipfs.add(file).get(0);
      
      //here to test writing to json file and reading to json file
      File f = new File("./src/Testing/ipfspracticesimple/nodes.json");
      FileWriter fw = new FileWriter(f);
      JSONObject obj = new JSONObject();
      obj.put("node", addResult.toJSON());
      //obj.put("node2", addResult.toJSONString());
      fw.append(obj.toJSONString());
      fw.flush();
      fw.close();
      
      
      
    } catch (IOException ex) {
      Logger.getLogger(IPFSPracticeSimple.class.getName()).log(Level.SEVERE, null, ex);
    }

    //To add byte[]
    NamedStreamable.ByteArrayWrapper file2
            = new NamedStreamable.ByteArrayWrapper("Hello2.txt", "Hello world, IPFS is awesome".getBytes());
    try {
      MerkleNode addResult = ipfs.add(file2).get(0);
      System.out.println(addResult.toJSONString());
      System.out.println(addResult.hash);
    } catch (IOException ex) {
      Logger.getLogger(IPFSPracticeSimple.class.getName()).log(Level.SEVERE, null, ex);
    }

    //read file --> using the hello2.txt hash
    Multihash filePointer = Multihash.fromBase58("QmP32obigPQgiNRaQ1HKfu8W4RUNnyDuwLAs2eyDebdbZy");
    try {
      byte[] fileContents = ipfs.cat(filePointer);
      System.out.println(new String(fileContents));
      
    } catch (IOException ex) {
      Logger.getLogger(IPFSPracticeSimple.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    
  }

}
