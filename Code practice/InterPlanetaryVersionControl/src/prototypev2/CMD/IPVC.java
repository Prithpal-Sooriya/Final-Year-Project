/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev2.CMD;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class to handle IPVS functions, requires the IPFS daemon to be running
 *
 * @author Prithpal Sooriya
 */
public class IPVC {

  private static final String SWARM_PEERS = "http://127.0.0.1:5001/api/v0/swarm/peers";
  private static final String ADD = "http://127.0.0.1:5001/api/v0/add";

  //constructor
  public IPVC() {

  }

  /**
   * returns JSON string of all the peers
   *
   * @return String JSON of all peers with open connections
   */
  public String peers() {
    String command = SWARM_PEERS;
    String JSONString = sendToDaemon(command, "");

    return JSONString;
  }

  /**
   *
   * @return String number of peers with open connections
   */
  public int peersNumber() {

    String command = SWARM_PEERS;
    String JSONString = sendToDaemon(command, "");
    System.out.println(JSONString);
    try {
      JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject) parser.parse(JSONString);
      JSONArray peers = (JSONArray) obj.get("Peers");
//      System.out.println(peers.toJSONString());
      return peers.size();
    } catch (ParseException ex) {
      System.err.println("Could not parse JSON String");
    }

    return 0;
  }

  public String addFile(String filepath) {
    String parameters = filepath;
    String jsonString = sendToDaemon(ADD, parameters);
    return jsonString;
  }


  
  
  //send command to daemon (through HTTP-API)
  private String sendToDaemon(String command, String parameters) {

    HttpURLConnection connection = null;
    try {
      URL url = new URL(command);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      //number of parmeters

      connection.setRequestProperty("Content-Length",
              Integer.toString(parameters.getBytes().length));

      connection.setRequestProperty("Content-Language", "en-GB");
      connection.setUseCaches(false);
      connection.setDoOutput(true); //to get json output

      //send request
      DataOutputStream dos = new DataOutputStream(
              connection.getOutputStream());
      dos.writeBytes(parameters);
      dos.flush();
      dos.close();

      //get response
      InputStream is = connection.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      StringBuilder response = new StringBuilder();
      String currentLine;

      //read whole responce
      while ((currentLine = br.readLine()) != null) {
        response.append(currentLine);
        response.append("\r");
      }
      br.close();
      return response.toString();

    } catch (MalformedURLException ex1) {
      System.err.println("URL given was malformed");
    } catch (IOException ex2) {
      //catch error from url.openConnection and protocolException
      System.err.println("io exception");
      ex2.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    System.out.println("Should not reach here");
    return "";
  }

  //main method here just for testing
  public static void main(String[] args) throws InterruptedException {
//    IPFS.startIPFSDaemon();

    IPVC ipvc = new IPVC();

    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      System.out.println(file.getAbsolutePath());
//      String output = ipvc.addFile(file.getAbsolutePath());
//      System.out.println(output);
    }
//    int output = ipvc.peersNumber();

//    IPFS.stopIPFSDaemon();
  }

}
