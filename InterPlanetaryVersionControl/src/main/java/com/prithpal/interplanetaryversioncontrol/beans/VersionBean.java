/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prithpal.interplanetaryversioncontrol.beans;

/**
 *
 * @author Prithpal Sooriya
 */
public class VersionBean {
  private static String dateSeperator = "T";
  private String date;
  private String commitMessage;
  private String author;
  private String hash;

  public VersionBean(String date, String commitMessage, String author, String hash) {
    this.date = date;
    this.commitMessage = commitMessage;
    this.author = author;
    this.hash = hash;
  }

  //TODO: remove unneccesary setters and getters
  public String getDate() {
    return date.replace("T", " ");
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getCommitMessage() {
    return commitMessage;
  }

  public void setCommitMessage(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }
}
