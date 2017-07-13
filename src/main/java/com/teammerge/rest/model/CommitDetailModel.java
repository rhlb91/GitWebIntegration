package com.teammerge.rest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.jgit.lib.PersonIdent;
@Entity
@Table(name="commitDetails")
public class CommitDetailModel {
  
  @Id
  @Column(name="commitAuthor")
  private PersonIdent commitAuthor;
  
  @Column(name="commitshortMessage")
  private String commitshortMessage;
  
  @Column(name="commitHash")
  private String commitHash;
  
  @Column(name="commitDate")
  private Date commitDate;
  
  @Column(name="branchId")
  private String branchId;
  
  @Override
  public String toString() {
    String str = "";
    str += ", commit Author: " + commitAuthor;
    str += ", short Msg: " + commitshortMessage;
    str += ", commit Hash: " + commitHash;
    str += ", commit Date: " + commitDate;
    str += ", branch Id: " + branchId;
    str += "<br>";
    return str;
  }
  
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
