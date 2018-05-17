/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author Prithpal
 */
public class TestMethods {

  private static void testEnvironmentVariables() {
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      System.out.format("%s=%s%n",
              envName,
              env.get(envName));
    }
  }

  public static void main(String[] args) throws IOException {
//    System.out.println(System.getProperty("java.class.path"));
//    System.out.println(System.getenv("PATH"));
//    System.out.println(System.getProperty("user.dir"));
    System.out.println(new File(".").getCanonicalPath());
  }

}
