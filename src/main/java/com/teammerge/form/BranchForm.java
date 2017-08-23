package com.teammerge.form;

public class BranchForm {

  private String branchId;

  private String shortName;

  private String repositoryId;
  
  private int numOfCommits;
  
  private int numOfPull;
  
  private String lastModifiedDate;

  /**
   * @return the branchId
   */
  public String getBranchId() {
    return branchId;
  }

  /**
   * @param branchId the branchId to set
   */
  public void setBranchId(String branchId) {
    this.branchId = branchId;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * @param the repositoryId
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(String repositoryId) {
    this.repositoryId = repositoryId;
  }
  
  public int getNumOfCommits() {
    return numOfCommits;
  }

  public void setNumOfCommits(int numOfCommits) {
    this.numOfCommits = numOfCommits;
  }

  public int getNumOfPull() {
    return numOfPull;
  }

  public void setNumOfPull(int numOfPull) {
    this.numOfPull = numOfPull;
  }

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

}
