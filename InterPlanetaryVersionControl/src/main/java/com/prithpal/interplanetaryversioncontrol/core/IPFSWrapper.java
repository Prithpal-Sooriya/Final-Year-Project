/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.core;

import com.prithpal.interplanetaryversioncontrol.CommandExecutor;
import com.prithpal.interplanetaryversioncontrol.Logger;
import com.prithpal.interplanetaryversioncontrol.OSUtilities;
import com.prithpal.interplanetaryversioncontrol.OSUtilities.OS_TYPE;
import com.prithpal.interplanetaryversioncontrol.UserInterface.MainUI;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Prithpal
 */
public class IPFSWrapper {

  private JFrame parentFrame;
  private Process IPFSProcess;
  private final Pattern IPFS_URL_PATTERN = Pattern.compile(
          "https?://[a-zA-Z0-9\\.\\-]+(:[0-9]{2,5})?/ipfs/[a-zA-Z0-9]{15,100}"
  );
  private final Pattern IPFS_HASH_PATTERN = Pattern.compile(
          "[a-zA-Z0-9]{15,100}"
  );

  //constructor
  public IPFSWrapper() {
    this.IPFSProcess = null;
    this.parentFrame = new JFrame();
  }

  public IPFSWrapper(JFrame parentFrame) {
    this.parentFrame = parentFrame;
    this.IPFSProcess = null;
  }

  public boolean isIPFSURL(String url) {
    return IPFS_URL_PATTERN.matcher(url).matches();
  }

  public void followIPFSLink(URL url) throws IOException, InterruptedException, URISyntaxException {
    if (this.ensureIPFSIsRunning()) {
      Logger.info("Opening IPFS link: {0}", url.toString());
      Desktop.getDesktop().browse(url.toURI());
    } else {
      Logger.info("Not opening IPFS link: {0} due to IPFS not running!", url.toString());
    }
  }

  private String getHashFromIPNSAdd(String result) {
    Matcher m = Pattern.compile("(Published to ([a-zA-Z0-9]{15,100}))").matcher(result);
    while(m.find()) {
      return m.group(2);
    }
    return null; //no match found
  }
  
  public String addToIPNS(String hash) {
    if (hash == null) {
      return null;
    }

    Cursor oldCursor = this.parentFrame.getCursor();
    try {
      String args[] = {this.getIPFSExecutable().getCanonicalPath(), "name", "publish", hash};
      CommandExecutor exec = new CommandExecutor(args);
      String response = exec.execute().trim();
      String ipnsHash = this.getHashFromIPNSAdd(response);
      Logger.info("File added, IPNS hash: " + ipnsHash);
      this.parentFrame.setCursor(oldCursor);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection("http://localhost:8080/ipns/" + ipnsHash), null);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "The hash " + ipnsHash + " has been added to the IPNS DHT.\n"
              + "It may be reached by other users (who also have IPFS server running)\n"
              + "via the link: http://localhost:8080/ipns/" + ipnsHash + "\n\n"
              + "you can copy the link from this text box and has already been copied to your clipboard",
              "File added success!",
              JOptionPane.INFORMATION_MESSAGE
      );
      return "[" + ipnsHash + "]("
              + "http://localhost:8080/ipns/" + ipnsHash + ")";

    } catch (Exception ex) {
      Logger.error("Unexpected Error: ", ex);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "Unexpected Error occured when adding file to IPFS network\n"
              + "" + ex.getMessage().replace(",", ",\n"),
              "Error when adding file",
              JOptionPane.ERROR_MESSAGE
      );
      return null;
    } finally {
      this.parentFrame.setCursor(oldCursor);
    }
  }

  //return in format [hash](link)
  //supports adding recursive and hidden (.) files (e.g. ".ipvc")
  public String addRecursiveFilesToIPFS(File file, boolean addHidden) {
    if (!file.exists()) {
      return null;
    }
    
    Cursor oldCursor = this.parentFrame.getCursor();
    try {
      this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Logger.info("Sharing file {0}", file.getCanonicalPath());

      if (!this.ensureIPFSIsRunning()) {
        return null;
      }

      String ipfs = this.getIPFSExecutable().getCanonicalPath();
      String content = OSUtilities.wrapString(file.getCanonicalPath());
      ArrayList<String> args = new ArrayList<>();
      args.add(ipfs);
      args.add("add");
      args.add("-r");
      args.add("-Q");
      if (addHidden) {
        args.add("-H");
      }
      args.add(content);

      CommandExecutor exec = new CommandExecutor(
              args.stream().toArray(String[]::new)
      );
      String response = exec.execute().trim();
      Logger.info("File added, IPFS hash: " + response);
      this.parentFrame.setCursor(oldCursor);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection("http://localhost:8080/ipfs/" + response), null);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "The folder " + file.getName() + " has been added to the IPFS network.\n"
              + "It may be reached by other users (who also have IPFS server running)\n"
              + "via the link: http://localhost:8080/ipfs/" + response + "\n\n"
              + "you can copy the link from this text box and has already been copied to your clipboard",
              "File added success!",
              JOptionPane.INFORMATION_MESSAGE
      );
      return "[" + response + "]("
              + "http://localhost:8080/ipfs/" + response + ")";
    } catch (Exception ex) {
      Logger.error("Unexpected Error: ", ex);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "Unexpected Error occured when adding file to IPFS network\n"
              + "" + ex.getMessage().replace(",", ",\n"),
              "Error when adding file",
              JOptionPane.ERROR_MESSAGE
      );
      return null;
    } finally {
      this.parentFrame.setCursor(oldCursor);
    }
  }

  //return will be in format [hash](link)
  //if error then return null
  public String addFileViaIPFS(File file) {
    if (!file.exists()) {
      return null;
    }

    Cursor oldCursor = this.parentFrame.getCursor();
    try {
      this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Logger.info("Sharing file {0}", file.getCanonicalPath());

      if (!this.ensureIPFSIsRunning()) {
        return null;
      }

      String ipfs = this.getIPFSExecutable().getCanonicalPath();
      String content = OSUtilities.wrapString(file.getCanonicalPath());

      CommandExecutor exec = new CommandExecutor(new String[]{
        ipfs, "add", "-Q", content
      });

      String response = exec.execute().trim();
      Logger.info("File added, IPFS hash: " + response);

      this.parentFrame.setCursor(oldCursor);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection("http://localhost:8080/ipfs/" + response), null);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "The file " + file.getName() + " has been added to the IPFS network.\n"
              + "It may be reached by other users (who also have IPFS server running)\n"
              + "via the link: http://localhost:8080/ipfs/" + response + "\n\n"
              + "you can copy the link from this text box and has already been copied to your clipboard",
              "File added success!",
              JOptionPane.INFORMATION_MESSAGE
      );
      return "[" + response + "]("
              + "http://localhost:8080/ipfs/" + response + ")";
    } catch (Exception ex) {
      Logger.error("Unexpected Error: ", ex);
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "Unexpected Error occured when adding file to IPFS network\n"
              + "" + ex.getMessage().replace(",", ",\n"),
              "Error when adding file",
              JOptionPane.ERROR_MESSAGE
      );
      return null;
    } finally {
      this.parentFrame.setCursor(oldCursor);
    }
  }

  private boolean startIPFS() throws IOException, InterruptedException {
    //warn user is IPFS is missing
    File ipfsExec = this.getIPFSExecutable();
    if (ipfsExec == null) {
      JOptionPane.showMessageDialog(
              this.parentFrame,
              "The IPFS executable is missing so cannot run!\n"
              + "Expected location: " + ipfsExec.getCanonicalPath(),
              "IPFS directory is not available",
              JOptionPane.ERROR_MESSAGE);
      return false;
    }

    this.init();

    this.startDaemon();

    return true;
  }

  private boolean getUserConsentForIPFS() throws IOException {
    String userDir = OSUtilities.getSettingsDirectory();
    File ipfsWarningFile = new File(userDir + File.separator + "ipfsWarning.flag");
    if (ipfsWarningFile.exists()) {
      return true; //allows a one time show of warning
    }

    Object[] options = {"Yes", "No", "Yes and do not show this message again"};
    int option = JOptionPane.showOptionDialog(
            this.parentFrame,
            "This program relies on IPFS (InterPlanetary File System).\n"
            + "It involves creating an IPFS server on your PC to enable distributed file sharing.\n"
            + "but as a result of this, all information shared will be available to any user who\n"
            + "hosts your data.\n"
            + "Before using this application, please make sure you fully understand the implications\n"
            + "on the IPFS website: https:ipfs.io\n"
            + "\n"
            + "IPFS Server requires ports 4001, 5001, and 8080 on the system for its own use.\n"
            + "The IPFS Server will be automatically stopped once you close this application\n"
            + "The startup of IPFS and accessing links on IPFS may start off slow (so as to \n"
            + "connect to other peers), so please be patient.n"
            + "\n"
            + "Do you wish to start an IPFS server on your PC?",
            "Confirm starting IPFS server...",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);

    if (option == 2) {
      ipfsWarningFile.createNewFile();
    }
    return (option != 1); //1 = no = false, 0 or 3 = yes = true

  }

  private String getIPFSExecutableName() {
    String ipfs = "ipfs";
    OS_TYPE os = OSUtilities.getOSType();
    if (os == OS_TYPE.WINDOWS) {
      ipfs += ".exe";
    }
    return ipfs;
  }

  //TODO: unsure howt to get IPFS directory if user has already installed it...
  //could install IPFS with this programl, but must be inside program directory.
  //currently, expect IPFS to be in program files
  private File getIPFSExecutable() throws IOException {
    return OSUtilities.getIPFSCommand(this.getIPFSExecutableName());
  }

  private void init() throws IOException, InterruptedException {
    File homeDir = OSUtilities.getUserHomeDirectory();
    File ipfsConfig = new File(homeDir, ".ipfs" + File.separator + "config");
    if (!ipfsConfig.exists()) {
      Logger.info("IPFS config file {0} does not exist. IPFS will be initialised", ipfsConfig.getCanonicalPath());
      CommandExecutor initialiser = new CommandExecutor(new String[]{
        this.getIPFSExecutable().getCanonicalPath(), "init"
      });

      String initResponse = initialiser.execute();
      Logger.info("IPFS initialisation messages {0}", initResponse);
    }
  }

  private boolean startDaemon() throws IOException, InterruptedException {
    //find IPFS and execute
    //TODO: add validation on IPFS path.
    CommandExecutor starter = new CommandExecutor(new String[]{
      this.getIPFSExecutable().getCanonicalPath(), "daemon", "--enable-pubsub-experiment", "--enable-namesys-pubsub"
    });

    this.IPFSProcess = starter.startChildProcess();

    //waiting period to ensure daemon is started
    //TODO: there must be a better way to ensure daemon is started (asych read when terminal shows "daemon is ready"
    Cursor prevCursor = this.parentFrame.getCursor();
    this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Thread.sleep(3000);
    this.parentFrame.setCursor(prevCursor);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        Logger.info("Stopping IPFS");
        try {
          IPFSWrapper.this.IPFSProcess.destroy();
          if (!IPFSWrapper.this.IPFSProcess.isAlive()) {
            IPFSWrapper.this.IPFSProcess = null;
          }
        } catch (Exception ex) {
          Logger.info("Could not stop IPFS");
        }
      }
    });
    return true;
  }

  private boolean ensureIPFSIsRunning() throws IOException, InterruptedException {
    if (!isIPFSRunning()) {
      if (!this.getUserConsentForIPFS()) {
        return false; //handle this from where it was called (the application should halt!)
      }
      return this.startIPFS();
    }
    return true; //ipfs is already running 
  }

  private boolean isIPFSRunning() {
    return IPFSProcess != null;
  }

  public String getHashFromIPFSAdd(String result) {
    //result is in format [hash](link)
    Matcher m = Pattern.compile("\\[" + IPFS_HASH_PATTERN + "\\]").matcher(result);
    while (m.find()) {
      return m.group(0).replace("[", "").replace("]", "");
    }
    return null; //could not be found
  }

}
