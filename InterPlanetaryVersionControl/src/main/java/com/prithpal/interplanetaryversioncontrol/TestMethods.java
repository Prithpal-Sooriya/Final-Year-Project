/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

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

  public static void main(String[] args) {
//    testEnvironmentVariables();
  }

}
