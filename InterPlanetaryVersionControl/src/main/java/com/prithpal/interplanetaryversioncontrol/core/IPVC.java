/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.core;

import com.prithpal.interplanetaryversioncontrol.CommandExecutor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.prithpal.interplanetaryversioncontrol.Logger;

/**
 *
 * @author Prithpal
 */
public class IPVC {

  private static final String IPVC_FOLDER = ".ipvc";
  private static final String COMMITS_JSON = "/.commits.json";
  private static final String COMMITS_HTML = "/index.html";
  private static final String COMMITS_JS = "/displayCommits.js";
  private static final String GIT_FOLDER = "/.git";
  private String ipnsHash = "";
  private IPFSWrapper ipfs;

  public IPVC(IPFSWrapper ipfs) {
    this.ipfs = ipfs;
  }

  public File searchForIPVCDirectory(File projectDirectory) {
    if (projectDirectory == null) {
      return null;
    }
    File file = new File(projectDirectory, "/.ipvc");
    if (file.exists()) {
      return file;
    }
    return null;
  }

  public String addIPFS(File f, String commitMessage, String author, String branchName) {
    //TODO: change error messages to Logger.error (utilise the error class)
    if (f == null) {
      System.err.println("IPVC - add(): folder given was null");
      return null;
    }
    if (!f.exists()) {
      System.err.println("IPVC - add(): folder given does not exist");
      return null;
    }
    if (!f.isDirectory()) {
      System.err.println("IPVC - add(): folder given was not a directory");
      return null;
    }

    File ipvcLocation = new File(f, IPVC_FOLDER);
    boolean firstCommit = !ipvcLocation.exists();
    if (firstCommit) {
      try {
        initIPVC(ipvcLocation);
      } catch (IOException ex) {
        System.err.println("IPVC - add(): Issue when creating IPVC folder");
      }
    }

    //return is in form [hash](url)
    String result = ipfs.addRecursiveFilesToIPFS(f, false);
    String hash = ipfs.getHashFromIPFSAdd(result);
    File commitsJSON = new File(ipvcLocation.getAbsolutePath() + COMMITS_JSON);
    try {
      String json = firstCommit
              ? VersionJSONCreator.addCommit(
                      FileUtilities.readFile(commitsJSON),
                      hash, commitMessage, author)
              : VersionJSONCreator.addCommit(
                      FileUtilities.readFile(commitsJSON),
                      hash, commitMessage, author, branchName);

      FileUtilities.writeFile(commitsJSON, json);

      createCommitsHTML(new File(ipvcLocation.getAbsolutePath() + COMMITS_HTML));
      createCommitsJS(new File(ipvcLocation.getAbsolutePath() + COMMITS_JS));
    } catch (IOException ex) {
//      Logger.getLogger(IPVC.class.getName()).log(Level.SEVERE, null, ex);
    }

    return hash;
  }

  //TODO: find a way to handle unix vs windows commands, currently using git directory to get unix commands
  public String addGitIPFS(File f) throws IOException, InterruptedException {
    String commandDirectoryGit = "C:\\tools\\git\\bin\\git";
    String commandDirectoryUnix = "C:\\tools\\git\\usr\\bin";

    if (f == null) {
      return null;
    }
    if (!f.exists()) {
      return null;
    }

    //find git folder
    File projectGitFolder = new File(f.getCanonicalPath() + GIT_FOLDER);
    if (projectGitFolder.exists()) {
      File projectGitTempStore = new File(FileUtilities.getSettingsDirectory() + "/" + f.getName() + ".git");

      //cp -r .git tempLocation
      final String args_cp_git[] = {
        commandDirectoryUnix + "/cp",
        "-r",
        projectGitFolder.getCanonicalPath(),
        projectGitTempStore.getCanonicalPath()
      };
      CommandExecutor exec = new CommandExecutor(args_cp_git);
      String result = exec.execute();
      if(result == null) {
        System.out.println("args_cp_git");
      }

      //cd <to new folder> && git update-server-info
      final String args_git_update_server_info[] = {
        commandDirectoryGit,
        "update-server-info"
      };
      exec = new CommandExecutor(args_git_update_server_info);
      result = exec.execute(projectGitTempStore.getCanonicalPath());
      if(result == null) {
        System.out.println("args_git_update_server_info");
      }
      
      //cp objects/pack/*.pack .
      final String args_cp_pack[] = {
        commandDirectoryUnix + "/cp",
        "objects/pack/*.pack",
        "."
      };
      exec = new CommandExecutor(args_cp_pack);
      result = exec.execute(projectGitTempStore.getCanonicalPath());
      if(result == null) {
        System.out.println("args_cp_pack");
      }
      
      //git unpack-objects < ./*.pack
      final String args_git_unpack_objects[] = {
        commandDirectoryGit,
        "unpack-objects",
        "<",
        "./*.pack"
      };
      exec = new CommandExecutor(args_git_unpack_objects);
      result = exec.execute(projectGitTempStore.getCanonicalPath());
      if(result == null) {
        System.out.println("args_git_unpack_objects");
      }
      
      //rm -f ./*.pack
      final String args_rm_pack[] = {
        commandDirectoryUnix + "/rm",
        "-f",
        "./*.pack"
      };
      exec = new CommandExecutor(args_rm_pack);
      result = exec.execute(projectGitTempStore.getCanonicalPath());
      if(result == null) {
        System.out.println("args_rm_pack");
      }
      
      String hash =  ipfs.addRecursiveFilesToIPFS(projectGitTempStore, false);
      
      //rm temp
      final String args_rm_temp[] = {
        commandDirectoryUnix + "/rm",
        "-r",
        "-f",
        projectGitTempStore.getName()
      };
      exec = new CommandExecutor(args_rm_temp);
      result = exec.execute(projectGitTempStore.getParent());
      if(result == null) {
        System.out.println("args_rm_temp");
      }
      
      return ipfs.getHashFromIPFSAdd(hash);
      
    } else {
      System.out.println("git does not exist!");
      System.out.println(projectGitFolder.getCanonicalPath());
    }

    return null;
  }

  public String addIPVCToIPNS(String ipfsHash) {
    return ipfs.addToIPNS(ipfsHash);
  }

  //this is just in case that IPNS is too slow!!!
  public String addIPVCToIPFS(File ipvcProjectDirectory) {
    if (ipvcProjectDirectory == null) {
      return null;
    }
    String result = ipfs.addRecursiveFilesToIPFS(ipvcProjectDirectory, true);
    return ipfs.getHashFromIPFSAdd(result);
  }

  private void initIPVC(File f) throws IOException {
    if (f.mkdir()) {
      System.out.println(f.getCanonicalPath());
      Path path = f.toPath();
      Files.setAttribute(path, "dos:hidden", true);
      File commitsJSON = new File(path.toString() + COMMITS_JSON);
      File commitsHTML = new File(path + COMMITS_HTML);
      File commitsJS = new File(path + COMMITS_JS);
      commitsJSON.createNewFile();
      commitsHTML.createNewFile();
      commitsJS.createNewFile();
      createCommitsJSON(commitsJSON);
      createCommitsHTML(commitsHTML);
      createCommitsJS(commitsJS);
    } else {
      System.err.println("IPVC - initIPVC(): Could not make IPVC folder at location " + f.getAbsolutePath());
    }
  }

  private void createCommitsJSON(File f) throws IOException {
    String json = VersionJSONCreator.initJSON();
    FileUtilities.writeFile(f, json);
  }

  private void createCommitsHTML(File f) throws IOException {
    String html = VersionSiteCreator.createHTML();
    FileUtilities.writeFile(f, html);
  }

  private void createCommitsJS(File f) throws IOException {
    String js = VersionSiteCreator.createJS(VersionSiteCreator.IPFS_LOCALHOST);
    FileUtilities.writeFile(f, js);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("testing git add");
    IPVC ipvc = new IPVC(new IPFSWrapper());
    String hash = ipvc.addGitIPFS(new File("C:\\Users\\Prithpal Sooriya\\Desktop\\Final-Year-Project"));
    System.out.println("hash: " + hash);
    System.exit(0);
  }
}
