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
import com.prithpal.interplanetaryversioncontrol.beans.VersionBean;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Prithpal
 */
public class IPVC {

  //TODO: refractor constants to suit either File(parent, filename) or File(filename) --> not both!!
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
    if (author.trim().isEmpty()) {
      System.err.println("IPVC - add(): author name was empty");
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
    } else {
      //validate ipvc branch name
      try {
        String json = FileUtilities.readFile(new File(ipvcLocation, ".commits.json"));
        boolean branchExists = VersionJSONCreator.branchExists(json, branchName.trim());
        if (!branchExists) {
          System.out.println("Branch does not exist!");
          return null;
        }
      } catch (IOException ex) {
        return null;
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
                      hash, commitMessage.trim(), author.trim())
              : VersionJSONCreator.addCommit(
                      FileUtilities.readFile(commitsJSON),
                      hash, commitMessage.trim(), author.trim(), branchName.trim());

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
      if (result == null) {
        System.out.println("args_cp_git");
      }

      //cd <to new folder> && git update-server-info
      final String args_git_update_server_info[] = {
        commandDirectoryGit,
        "update-server-info"
      };
      exec = new CommandExecutor(args_git_update_server_info);
      result = exec.execute(projectGitTempStore.getCanonicalPath());
      if (result == null) {
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
      if (result == null) {
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
      if (result == null) {
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
      if (result == null) {
        System.out.println("args_rm_pack");
      }

      String hash = ipfs.addRecursiveFilesToIPFS(projectGitTempStore, false);

      //rm temp
      final String args_rm_temp[] = {
        commandDirectoryUnix + "/rm",
        "-r",
        "-f",
        projectGitTempStore.getName()
      };
      exec = new CommandExecutor(args_rm_temp);
      result = exec.execute(projectGitTempStore.getParent());
      if (result == null) {
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

  public boolean createBranch(File projectDirectory, String currentBranch, String newBranch, String commitMessage, String author) {
    //validation 
    if (currentBranch.trim().isEmpty()) {
      return false;
    }
    if (newBranch.trim().isEmpty()) {
      return false;
    }
    if (commitMessage.trim().isEmpty()) {
      return false;
    }
    if (author.trim().isEmpty()) {
      return false;
    }

    File ipvcFolder = searchForIPVCDirectory(projectDirectory);
    if (ipvcFolder == null) {
      System.err.println("project folder does not contains ipvc folder");
      return false;
    }
    if (ipvcFolder.exists() && ipvcFolder.isDirectory()) {
      File commitsJSON = new File(ipvcFolder + COMMITS_JSON);
      try {
        String json = FileUtilities.readFile(commitsJSON);
        if (VersionJSONCreator.branchExists(json, newBranch.trim())) {
          System.err.println("IPVC - createBranch: branch exists!");
          return false;
        }
        json = VersionJSONCreator.addBranch(json, currentBranch, newBranch, commitMessage, author);
        if (json == null) {
          return false;
        }
        FileUtilities.writeFile(commitsJSON, json);
        return true;
      } catch (Exception ex) {
        System.err.println("IPVC - create branch: could not read json");
      }
    }
    return false;
  }

  public boolean deleteBranch(File projectDirectory, String branch) {
    File ipvcFolder = searchForIPVCDirectory(projectDirectory);
    if (ipvcFolder == null) {
      System.err.println("project folder does not contains ipvc folder");
      return false;
    }
    if (ipvcFolder.exists() && ipvcFolder.isDirectory()) {
      File commitsJSON = new File(ipvcFolder + COMMITS_JSON);
      try {
        String json = FileUtilities.readFile(commitsJSON);
        if (!VersionJSONCreator.branchExists(json, branch.trim())) {
          System.err.println("IPVC - createBranch: branch does not exists!");
          return false;
        }
        json = VersionJSONCreator.deleteBranch(json, branch);
        if (json == null) {
          return false;
        }
        FileUtilities.writeFile(commitsJSON, json);
        return true;
      } catch (IOException ex) {
        System.err.println("IPVC - create branch: could not read json");
        return false;
      }
    }
    return false;
  }

  public boolean mergeBranch(File projectDirectory, String sourceBranch, String destinationBranch, String commitMessage, String author, boolean deleteBranch) {
    File ipvcFolder = searchForIPVCDirectory(projectDirectory);
    if (ipvcFolder == null) {
      System.err.println("project folder does not contains ipvc folder");
      return false;
    }
    if (ipvcFolder.exists() && ipvcFolder.isDirectory()) {
      File commitsJSON = new File(ipvcFolder + COMMITS_JSON);
      try {
        String json = FileUtilities.readFile(commitsJSON);
        if (!VersionJSONCreator.branchExists(json, sourceBranch.trim())) {
          System.err.println("IPVC - mergeBranch: sourceBranch does not exists!");
          return false;
        }
        if (!VersionJSONCreator.branchExists(json, destinationBranch.trim())) {
          System.err.println("IPVC - mergeBranch: sourceBranch does not exists!");
          return false;
        }
        json = VersionJSONCreator.mergeBranch(json, sourceBranch.trim(), destinationBranch.trim(), commitMessage.trim(), author.trim(), deleteBranch);
        if (json == null) {
          return false;
        }
        FileUtilities.writeFile(commitsJSON, json);
        return true;
      } catch (IOException ex) {
        System.err.println("IPVC - merge branch: could not read json");
        return false;
      }
    }
    return false;
  }

  public VersionBean getLatestVersion(File projectDirectory, String branch) {
    File ipvcDirectory = searchForIPVCDirectory(projectDirectory);
    if (ipvcDirectory == null) {
      System.err.println("IPVC - getLatestVersion(): ipvc directory not found in project");
      return null;
    }
    if (branch == null) {
      return null;
    }

    if (ipvcDirectory.exists() && ipvcDirectory.isDirectory()) {
      File commitsJSON = new File(ipvcDirectory + COMMITS_JSON);
      try {
        String json = FileUtilities.readFile(commitsJSON);
        VersionBean head = null;
        if (VersionJSONCreator.branchExists(json, branch)) {
          head = VersionJSONCreator.getBranchHead(json, branch);
        }
        return head; //will be null if the branch does not exist...

      } catch (Exception ex) {
        System.err.println("IPVC - get latest version: could not read json");
      }
    }
    return null;

  }

  public List<VersionBean> getHistory(File projectDirectory, String branch) {
    File ipvcDirectory = searchForIPVCDirectory(projectDirectory);
    if (ipvcDirectory == null) {
      System.err.println("IPVC - getLatestVersion(): ipvc directory not found in project");
      return null;
    }
    if (branch == null) {
      return null;
    }

    if (ipvcDirectory.exists() && ipvcDirectory.isDirectory()) {
      File commitsJSON = new File(ipvcDirectory + COMMITS_JSON);
      try {
        String json = FileUtilities.readFile(commitsJSON);
        if (VersionJSONCreator.branchExists(json, branch)) {
          return VersionJSONCreator.getBranchVersions(json, branch);
        }
      } catch (IOException ex) {
        System.err.println("IPVC - get branch history: could not read json");
      }
    }
    return null;
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
