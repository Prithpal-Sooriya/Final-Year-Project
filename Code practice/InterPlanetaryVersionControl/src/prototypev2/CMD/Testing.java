/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypev2.CMD;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.simple.JSONObject;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Prithpal Sooriya
 */
public class Testing {

  //using byte array
  private static void sendToDaemon() throws MalformedURLException, IOException {
    //connect to daemon
    URL url = new URL("http://127.0.0.1:5001/api/v0/add");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //get file to upload
//    String filePath = "C:\\Users\\Prithpal Sooriya\\Desktop\\rose-blue-flower-rose-blooms-67636.jpeg";
    String filePath = "C:\\Users\\Prithpal Sooriya\\Desktop\\flower";
    File fileToUpload = new File(filePath);

    //set some metadata
    String param = "recursive=true";
    String charset = "UTF-8";
    String boundary = Long.toHexString(System.currentTimeMillis()); //unique boundry...
    String CRLF = "\r\n"; //line seperator

    //write to HTTP Request Body
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    //contents to write to request
    try (
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {

      //sending a normal parameter
//      writer.append("--" + boundary).append(CRLF);
//      writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
//      writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
//      writer.append(CRLF).append(param).append(CRLF).flush();
//
//      //sending a text file
//      writer.append("--" + boundary).append(CRLF);
//      writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"" + fileToUpload.getName() + "\"").append(CRLF);
//      writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
//      writer.append(CRLF).flush();
//      Files.copy(fileToUpload.toPath(), output);
//      output.flush();
//      writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
      // Send binary file.
      writer.append("--" + boundary).append(CRLF);
//      writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + fileToUpload.getName() + "\"").append(CRLF);
      writer.append("Content-Disposition: file; filename=\"" + fileToUpload.getName() + "\"").append(CRLF);
      writer.append("Content-Type: " + connection.guessContentTypeFromName(fileToUpload.getName())).append(CRLF);
      writer.append("Content-Transfer-Encoding: binary").append(CRLF);
      writer.append(CRLF).flush();
//      if(fileToUpload.isDirectory()) {
//        
//        System.out.println(fileToUpload.toPath());
//        System.exit(0);
//      }
      Files.copy(fileToUpload.toPath(), output);
      output.flush(); // Important before continuing with writer!
      writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

      // End of multipart/form-data.
      writer.append("--" + boundary + "--").append(CRLF).flush();

      //printwriter to get info!!
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
      System.out.println(response.toString());

    }
  }

  //using zipping
  private static void sendToDaemon2() throws MalformedURLException, IOException {
    //connect to daemon
    URL url = new URL("http://127.0.0.1:5001/api/v0/add");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //folder
    String filePath = "./src/flower/flower.jpeg";
//    String filePath = "C:\\Users\\Prithpal Sooriya\\Desktop\\flower";

    //http POST metadata
    String param = "recursive=true";
    String charset = "UTF-8";
    String boundary = Long.toHexString(System.currentTimeMillis()); //unique boundry...
    String CRLF = "\r\n"; //line seperator

    //http post header
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    //contents to write to request
    try (
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {
      // Send binary file.
      Path p = Paths.get(filePath);
      File f = new File(filePath);
      //convert file to binary
//      byte[] data = Files.readAllBytes(p);

      // Send binary file.
      writer.append("--" + boundary).append(CRLF);
      writer.append("Content-Disposition: file; filename=\"" + f.getName() + "\"").append(CRLF);
      writer.append("Content-Type: " + connection.guessContentTypeFromName(f.getName())).append(CRLF);
      writer.append("Content-Transfer-Encoding: binary").append(CRLF);
      writer.append(CRLF).flush();

      //write file to output stream
      byte[] buffer = new byte[4096];
      System.out.println(f);
      FileInputStream fin = new FileInputStream(f);
      ByteOutputStream baos = new ByteOutputStream();
      ZipOutputStream zout = new ZipOutputStream(baos);
      zout.putNextEntry(new ZipEntry(filePath));

      int length;
      while ((length = fin.read(buffer)) > 0) {
        zout.write(buffer, 0, length);
      }
      zout.closeEntry();
      fin.close();
      byte[] data = baos.toByteArray();

      output.write(data);
      output.flush(); // Important before continuing with writer!
      writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

      // End of multipart/form-data.
      writer.append("--" + boundary + "--").append(CRLF).flush();

      //printwriter to get info!!
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
      System.out.println(response.toString());
    }
  }

  private static void sendToDaemon3() throws MalformedURLException, IOException {
    //connect to daemon
    URL url = new URL("http://127.0.0.1:5001/api/v0/add");
//    URL url = new URL("http://127.0.0.1:5001/api/v0/add?" + "arg=" + "C:\\Users\\Prithpal Sooriya\\Desktop\\flower" + "&recursive==true");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //folder
    String filePath = "./src/flower/flower.jpeg";
//    String filePath = "C:\\Users\\Prithpal Sooriya\\Desktop\\flower";
    File f = new File(filePath);

    connection.setRequestMethod("POST");
    /* header:
    should look like this:
    'Content-Type: multipart/form-data; boundary=CUSTOM'
     */
    String boundary = Long.toHexString(System.currentTimeMillis());

    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    /* body:
    should look like this:
    '--CUSTOM\r\nContent-Type: multipart/octet-stream\r\nContent-Disposition: file; filename="test"\r\n\r\nHello World!\n--CUSTOM--'
     */
    String param = "recursive=true";
    String CRLF = "\r\n"; //line seperator

    connection.setDoOutput(true);
    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(out), true);
//    writer.append("curl -i -h");
    writer.append("--" + boundary).append(CRLF);
    writer.append("Content-Type: multipart/octet-stream").append(CRLF);
    writer.append("Content-Disposition: file; filename: " + f.getName()).append(CRLF).append(CRLF);
    writer.flush();

    out.write(loadFileInMemory(f));
    out.flush();
    out.close();
    writer.append(CRLF);
    writer.append("--" + boundary + "--").append(CRLF);
    writer.flush();

    System.out.println();

//    int responseCode = connection.getResponseCode();
//    System.out.println("responce code = " + responseCode);
//    connection.setDoInput(true);
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
    System.out.println(response.toString());
  }

  private static byte[] loadFileInMemory(File f) throws IOException {
    RandomAccessFile oRAF = null;
    try {
      oRAF = new RandomAccessFile(f, "r");
      byte bytes[] = new byte[(int) oRAF.length()];
      oRAF.readFully(bytes);
      return bytes;
    } finally {
      if (oRAF != null) {
        oRAF.close();
      }
    }
  }

  private static void sendToDaemon4() throws MalformedURLException, IOException {
    //connect to daemon
    URL url = new URL("http://127.0.0.1:5001/api/v0/add?arg=C:\\Users\\\"Prithpal Sooriya\"\\Desktop\\flower&recursive=true");
//    URL url = new URL("http://127.0.0.1:5001/api/v0/add?" + "arg=" + "C:\\Users\\Prithpal Sooriya\\Desktop\\flower" + "&recursive==true");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    String path = "C:\\Users\\Prithpal Sooriya\\Desktop\\flower";
    String args = "arg=" + path + "&recursive=true";
    File f = new File(path);

    String boundary = UUID.randomUUID().toString();
    byte[] boundaryBytes
            = ("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8);
    byte[] finishBoundaryBytes
            = ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);
    connection.setRequestMethod("POST");
    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    //chunking with default setting... maybe
    connection.setChunkedStreamingMode(0);
    connection.setDoOutput(true);
    OutputStream out = connection.getOutputStream();
    
    //begin sending!
    //Content-Type: multipart/octet-stream\r\nContent-Disposition: file; filename="test"\r\n\r\nHello World!\n
    out.write(boundaryBytes); //send header
    String a = "Content-Type: multipart/octet-stream\r\n";
    String b = "Content-Disposition: file; filename=\"" + URLEncoder.encode(path) + "\"\r\n\r\n";
    out.write(a.getBytes(StandardCharsets.UTF_8));
    out.write(b.getBytes(StandardCharsets.UTF_8));
    out.write(finishBoundaryBytes); //send finish request

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
    System.out.println(response.toString());
  }

  public static void main(String[] args) {
    try {
      sendToDaemon4();
    } catch (IOException ex) {
      Logger.getLogger(Testing.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
