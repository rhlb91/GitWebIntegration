package com.teammerge.rest.model;

import java.util.Date;

public class BranchDetailModel {
 
  
  
  private String branchId;
  private int numOfCommits;
  private int numOfPull;
  

  private int numOfBranches;
  private String lastModifiedDate;
  private String repositaryId;
  
  @Override
  public String toString() {
   StringBuilder str = new StringBuilder();
   str.append("branch Id:- " + getBranchId());
   str.append(" num Of Commits:- " + getNumOfCommits());
   str.append(" num Of Pull:- " + getNumOfPull());
   str.append(" num Of Branches:- " + getNumOfBranches());
   str.append(" last Modified Date:- " + getLastModifiedDate());
   str.append(" repositary Id:- " + getRepositaryId());
   return str.toString();
  }
  
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

  public int getNumOfPull() {
    return numOfPull;
  }

  public void setNumOfPull(int numOfPull) {
    this.numOfPull = numOfPull;
  }

  public int getNumOfBranches() {
    return numOfBranches;
  }

  public void setNumOfBranches(int numOfBranches) {
    this.numOfBranches = numOfBranches;
  }

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getRepositaryId() {
    return repositaryId;
  }

  public void setRepositaryId(String repositaryId) {
    this.repositaryId = repositaryId;
  }
}
