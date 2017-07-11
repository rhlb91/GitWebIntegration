package com.teammerge.rest.model;

import java.util.Date;

public class BranchDetailModel {
  
  private String branchId;
  private int numOfCommits;
  private int numOfPulls;
  private int numofBranches;
  private Date lastModifiedDate;
  private String repositaryId;
  
  public String getBranchId() {
    return branchId;
  }
  public void setBranchId(String branchId) {
    this.branchId = branchId;
  }
  public int getNumOfCommits() {
    return numOfCommits;
  }
  public void setNumOfCommits(int numOfCommits) {
    this.numOfCommits = numOfCommits;
  }
  public int getNumOfPulls() {
    return numOfPulls;
  }
  public void setNumOfPulls(int numOfPulls) {
    this.numOfPulls = numOfPulls;
  }
  public int getNumofBranches() {
    return numofBranches;
  }
  public void setNumofBranches(int numofBranches) {
    this.numofBranches = numofBranches;
  }
  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }
  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
  public String getRepositaryId() {
    return repositaryId;
  }
  public void setRepositaryId(String repositaryId) {
    this.repositaryId = repositaryId;
  }
}
