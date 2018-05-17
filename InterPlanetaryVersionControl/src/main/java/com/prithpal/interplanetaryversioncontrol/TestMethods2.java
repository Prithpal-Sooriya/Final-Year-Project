/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Prithpal
 */
public class TestMethods2 {
  public static void main(String[] args) {
    String str = "Published to QmVHHBGtK6mSz5PLqmBHSa6yEvLFTAvkMcM7Dj7RpjAjZ4: /ipfs/QmZheZ8unsLC5cbeGazVpWcYnxTAAA4gCeSyADeK7cK9BG";
    Matcher m = Pattern.compile("(Published to ([a-zA-Z0-9]{15,100}))").matcher(str);
    while(m.find()) {
      System.out.println(m.group(2));
    }
  }
}
