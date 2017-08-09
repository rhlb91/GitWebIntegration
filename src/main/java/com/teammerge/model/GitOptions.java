package com.teammerge.model;



public class GitOptions {
  private String URI;
  private String destinationDirectory;
  private boolean isCloneAllBranches;
  private boolean includeSubModule;
  private boolean isBare;
  private String username;
  private String password;


  public GitOptions() {
    this("", "", true, true);
  }

  public GitOptions(String uri, String dir) {
    this(uri, dir, true, true);
  }

  public GitOptions(String uri, String dir, boolean cloneAllBranches, boolean includeSubModule) {
    this.URI = uri;
    this.setDestinationDirectory(dir);
    this.isCloneAllBranches = cloneAllBranches;
  }

  public String getURI() {
    return URI;
  }

  public void setURI(String uRI) {
    URI = uRI;
  }


  public boolean isCloneAllBranches() {
    return isCloneAllBranches;
  }

  public void setCloneAllBranches(boolean isCloneAllBranches) {
    this.isCloneAllBranches = isCloneAllBranches;
  }

  public boolean isIncludeSubModule() {
    return includeSubModule;
  }

  public void setIncludeSubModule(boolean includeSubModule) {
    this.includeSubModule = includeSubModule;
  }

  public String getDestinationDirectory() {
    return destinationDirectory;
  }

  public void setDestinationDirectory(String destinationDirectory) {
    this.destinationDirectory = destinationDirectory;
  }

  public boolean isBare() {
    return isBare;
  }

  public void setBare(boolean isBare) {
    this.isBare = isBare;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


}
