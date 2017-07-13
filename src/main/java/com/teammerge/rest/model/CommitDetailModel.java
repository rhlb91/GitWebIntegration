package com.teammerge.rest.model;

import java.util.Date;

import org.eclipse.jgit.lib.PersonIdent;

public class CommitDetailModel {

  private PersonIdent commitAuthor;
  private String commitshortMessage;
  private String commitHash;
  private Date commitDate;
  private String branchId;
  
  public PersonIdent getCommitAuthor() {
    return commitAuthor;
  }
  public void setCommitAuthor(PersonIdent commitAuthor) {
    this.commitAuthor = commitAuthor;
  }
  public String getCommitshortMessage() {
    return commitshortMessage;
  }
  public void setCommitshortMessage(String commitshortMessage) {
    this.commitshortMessage = commitshortMessage;
  }
  public String getCommitHash() {
    return commitHash;
  }
  public void setCommitHash(String commitHash) {
    this.commitHash = commitHash;
  }
  public Date getCommitDate() {
    return commitDate;
  }
  public void setCommitDate(Date commitDate) {
    this.commitDate = commitDate;
  }
  public String getBranchId() {
    return branchId;
  }
  public void setBranchId(String branchId) {
    this.branchId = branchId;
  }
  
}
