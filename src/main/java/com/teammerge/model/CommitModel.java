package com.teammerge.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.jgit.lib.PersonIdent;

@Entity
@Table(name = "commit_details")
public class CommitModel implements Serializable, Comparable<CommitModel> {

  private static final long serialVersionUID = 6471094113056162919L;

  @Id
  @Column(name = "commit_id")
  private String commitId;
  
  @Column(name = "commit_author")
  private PersonIdent commitAuthor;

  @Column(name = "short_Message")
  private String shortMessage;

  @Column(name = "trimmed_Message")
  private String trimmedMessage;
  
  @Column(name = "commit_Hash")
  private String commitHash;
  
  @Column(name = "isMerge_Commit")
  private Boolean isMergeCommit;
  
  @Column(name = "commit_Date")
  private Date commitDate;
  
  @Column(name = "commitTimeFormatted")
  private String commitTimeFormatted;



  private String repositoryName;

  private String branchName;

  @Override
  public String toString() {
    String str = "";
    str += "Commit Id: " + commitId;
    str += ", commit Author: " + commitAuthor;
    str += ", short Msg: " + shortMessage;
    str += ", trimmed Msg: " + trimmedMessage;
    str += ", commit Hash: " + commitHash;
    str += ", commit Date: " + commitDate;
    str += "<br>";
    return str;
  }


  public String getShortMessage() {
    return shortMessage;
  }

  public void setShortMessage(String shortMessage) {
    this.shortMessage = shortMessage;
  }

  public String getTrimmedMessage() {
    return trimmedMessage;
  }

  public void setTrimmedMessage(String trimmedMessage) {
    this.trimmedMessage = trimmedMessage;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public void setCommitHash(String commitHash) {
    this.commitHash = commitHash;
  }

  public PersonIdent getCommitAuthor() {
    return commitAuthor;
  }

  public void setCommitAuthor(PersonIdent commitAuthor) {
    this.commitAuthor = commitAuthor;
  }

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public Boolean getIsMergeCommit() {
    return isMergeCommit;
  }

  public void setIsMergeCommit(Boolean isMergeCommit) {
    this.isMergeCommit = isMergeCommit;
  }

  public Date getCommitDate() {
    return commitDate;
  }

  public void setCommitDate(Date commitDate) {
    this.commitDate = commitDate;
  }

  @Override
  public int compareTo(CommitModel o) {
    // reverse-chronological order
    if (commitDate.after(o.getCommitDate())) {
      return -1;
    } else if (commitDate.before(o.getCommitDate())) {
      return 1;
    }
    return 0;
  }

  public String getCommitTimeFormatted() {
    return commitTimeFormatted;
  }

  public void setCommitTimeFormatted(String commitTimeFormatted) {
    this.commitTimeFormatted = commitTimeFormatted;
  }

  public String getRepositoryName() {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }


}
