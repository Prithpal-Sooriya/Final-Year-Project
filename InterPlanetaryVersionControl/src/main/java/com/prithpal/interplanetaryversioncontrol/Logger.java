/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Logger class that will log contents (success/warning/error) onto a log file
 *
 * @author Prithpal
 */
public class Logger {

  private static final String DEBUG = "DEBUG";
  private static final String TRACE = "TRACE";
  private static final String INFO = "INFO";
  private static final String WARNING = "WARNING";
  private static final String ERROR = "ERROR";

  private static PrintStream outputFile;
  private static Set<String> oneTimeMessages = new HashSet<>();

  //static initialization block (will be the first think to run/initialised
  //used to create and update a file
  static {
    try {
      String settingsDir = OSUtilities.getSettingsDirectory();
      Calendar currentDate = new GregorianCalendar();
      Date date = new Date();
      currentDate.setTime(date);

      String logFile = settingsDir + File.separator + "IPVC_"
              + (int) (currentDate.get(Calendar.YEAR)) + "_"
              + (int) (currentDate.get(Calendar.MONTH)) + "_"
              + (int) (currentDate.get(Calendar.DAY_OF_MONTH)) + "_"
              + "debug.log";
      outputFile = new PrintStream(new FileOutputStream(logFile, true));
    } catch (IOException ex) {
      outputFile = null;
      System.out.println("Error in initialising logging!");
      ex.printStackTrace();
    }
  }

  //...args = multiple arguments 
  public static void debug(String message, Object... args) {
    printMessage(DEBUG, message, null, args);
  }

  public static void trace(String message, Object... args) {
    printMessage(TRACE, message, null, args);
  }

  public static void info(String message, Object... args) {
    printMessage(INFO, message, null, args);
  }

  public static void warning(String message, Object... args) {
    warning(message, null, args);
  }

  public static void warning(String message, Throwable t, Object... args) {
    printMessage(WARNING, message, t, args);
  }

  public static void warningOneTime(String message, Object... args) {
    printMessage(true, WARNING, message, null, args);
  }

  public static void error(String message, Object... args) {
    error(message, null, args);
  }

  public static void error(String message, Throwable t, Object... args) {
    printMessage(ERROR, message, t, args);
  }

  private static void printMessage(String messageClass, String message, Throwable t, Object... args) {
    printMessage(false, messageClass, message, t, args);
  }

  private static void printMessage(boolean oneTimeOnly, String messageClass, String message, Throwable t, Object... args) {
    //stringify message
    for (int i = 0; i < args.length; i++) {
      if (args[i] != null) {
        message = message.replace("{" + i + "}", args[i].toString());
      }
    }
    message += " ";

    //log one time messages (into this location)
    if (oneTimeOnly) {
      if (oneTimeMessages.contains(message)) {
        return;
      } else {
        oneTimeMessages.add(message);
      }
    }

    //prefix and message class messages
    String prefix = '[' + Thread.currentThread().getName() + ']'
            + '[' + (new Date().toString()) + ']';
    messageClass = '[' + messageClass + ']';

    //log throwable
    String throwable = "";
    if (t != null) {
      CharArrayWriter caw = new CharArrayWriter(1024);
      PrintWriter pw = new PrintWriter(caw);
      pw.println();
      t.printStackTrace(pw);
      pw.close();
      throwable = new String(caw.toCharArray());
    }

    System.out.println(prefix + messageClass + message + throwable);
    if (outputFile != null) {
      outputFile.println(prefix + messageClass + message + throwable);
      outputFile.flush();
    }
  }
}
