package com.teammerge.model;

import org.eclipse.jgit.lib.Repository;

public class CreateBranchOptions {
  private Repository repo;
  private String branchName;
  private String companyName;
  private String userName;
  private String password;
  private String remoteURL;
  /**
   * @return the repo
   */
  public Repository getRepo() {
    return repo;
  }
  /**
   * @param repo the repo to set
   */
  public void setRepo(Repository repo) {
    this.repo = repo;
  }
  /**
   * @return the branchName
   */
  public String getBranchName() {
    return branchName;
  }
  /**
   * @param branchName the branchName to set
   */
  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }
  /**
   * @return the companyName
   */
  public String getCompanyName() {
    return companyName;
  }
  /**
   * @param companyName the companyName to set
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }
  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }
  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  /**
   * @return the remoteURL
   */
  public String getRemoteURL() {
    return remoteURL;
  }
  /**
   * @param remoteURL the remoteURL to set
   */
  public void setRemoteURL(String remoteURL) {
    this.remoteURL = remoteURL;
  }

}
