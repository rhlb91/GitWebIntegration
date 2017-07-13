package com.teammerge.rest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="branchDetails")
public class BranchDetailModel {
 
  @Id
  @Column(name = "branchId")
  private String branchId;
  
  @Column(name = "numOfCommits")
  private int numOfCommits;
  
  @Column(name = "numOfPull")
  private int numOfPull;
  
  
  
  @Column(name = "lastModifiedDate")
  private String lastModifiedDate;
  
  @Column(name = "repositaryId")
  private String repositaryId;
  
  @Override
  public String toString() {
   StringBuilder str = new StringBuilder();
   str.append("branch Id:- " + getBranchId());
   str.append(" num Of Commits:- " + getNumOfCommits());
   str.append(" num Of Pull:- " + getNumOfPull());
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
