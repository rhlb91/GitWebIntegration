package com.teammerge.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "branch_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
public class BranchModel implements Serializable {

  private static final long serialVersionUID = 1100490355473736524L;

  @Id
  @Column(name = "branch_id")
  private String branchId;

  @Id
  @Column(name = "short_name")
  private String shortName;
  
  @Column(name = "num_Of_commits")
  private int numOfCommits;

  @Column(name = "num_Of_pulls")
  private int numOfPull;

  @Column(name = "last_modified_date")
  private String lastModifiedDate;

  @Column(name = "repository_id")
  private String repositoryId;

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("branch Id:- " + getBranchId());
    str.append(" num Of Commits:- " + getNumOfCommits());
    str.append(" num Of Pull:- " + getNumOfPull());
    str.append(" last Modified Date:- " + getLastModifiedDate());
    str.append(" repositary Id:- " + getRepositoryId());
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

  public String getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(String repositaryId) {
    this.repositoryId = repositaryId;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}
