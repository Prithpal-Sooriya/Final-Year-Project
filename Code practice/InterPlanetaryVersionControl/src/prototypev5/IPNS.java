/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for creating IPNS namespace
 *
 * @author Prithpal Sooriya
 */
public class IPNS {

  public static String add(String hash) {
    if (hash == null) {
      System.err.println("IPNS - add(): ipfs hash was null");
      return null;
    }

    String command = "ipfs name publish /ipfs/" + hash;
    try {
      System.out.println("Running IPNS command, please wait");
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();

      System.out.println("IPNS finished:");
      String lastLine = "";
      String currentLine;
      BufferedReader output = new BufferedReader(
        new InputStreamReader(p.getInputStream()));
      while ((currentLine = output.readLine()) != null) {
        lastLine = currentLine;
      }
//      System.out.println(lastLine);
      //output is in format
      //"Published to QmQRwPobaybNWbixKr5HFejGq83WeYHyCU1JTk6ssYdxa3: /ipfs/QmfSNrnj3zaCehEcmbQj77mAG4ZnBzG282wGhPQwa8ia2q"
      //we want the Qm....a3 hash
      String ipnsHash = lastLine.replace("Published to ", "");
      ipnsHash = ipnsHash.substring(0, ipnsHash.indexOf(": "));
//      System.out.println(ipnsHash);
      return ipnsHash;
    } catch (IOException ex) {
      Logger.getLogger(IPNS.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(IPNS.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static void main(String[] args) {
//    IPVC ipvc = new IPVC();
//    System.out.println("Waiting for IPNS");
//    String ipnsHash = IPNS.add("QmZheZ8unsLC5cbeGazVpWcYnxTAAA4gCeSyADeK7cK9BG");
//    System.out.println(ipnsHash);

//  String lastLine = "Published to QmQRwPobaybNWbixKr5HFejGq83WeYHyCU1JTk6ssYdxa3: /ipfs/QmfSNrnj3zaCehEcmbQj77mAG4ZnBzG282wGhPQwa8ia2q";
    String lastLine = "Published to QmRKMCk827FTrTAdfpk1eJJW64xJWB2sKx6KeeDG2gb1RP: /ipfs/QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn";
    String ipnsHash = lastLine.replace("Published to ", "");
    ipnsHash = ipnsHash.substring(0, ipnsHash.indexOf(": "));
    System.out.println(ipnsHash);

  }

}
