/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Class responsible for executing terminal commands
 *
 * @author Prithpal
 */
public class CommandExecutor {

  private String args[];

  //constructor
  public CommandExecutor(String args[]) throws IOException {
    this.args = args;
  }

  //create a child process (to run commands in background)
  public Process startChildProcess() throws IOException {
    return Runtime.getRuntime().exec(args);
  }

  //execute command
  public String execute() throws IOException, InterruptedException {
    final StringBuffer result = new StringBuffer();
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(args);

    final Reader in = new InputStreamReader(new BufferedInputStream(p.getInputStream()));
    final Reader err = new InputStreamReader(p.getErrorStream());

    //thread for handle reading terminal output
    Thread execInThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          int c;
          while ((c = in.read()) != -1) {
            result.append((char) c);
          }
        } catch (IOException ex) {
          //error when reading
          //need to find a good way to handle this...
        }
      }
    });
    execInThread.start();

    //thread for handle reading terminal error
    Thread execErrThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          int c;
          while ((c = err.read()) != -1) {
            result.append((char) c);
          }
        } catch (IOException ex) {
          //error when reading
          //need to find a good way to handle this...
        }
      }
    });
    execErrThread.start();
    
    p.waitFor(); //wait for process to end (thus the result/String Buffer is fully complete
    execInThread.join(); //wait for 'in' to die
    execErrThread.join(); //wait for 'exec' to die.
    
    return result.toString();
  }
}
