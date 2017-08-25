package com.teammerge.form;

import java.io.Serializable;

public class CommitForm implements Serializable {

  private static final long serialVersionUID = 7392361412042980259L;
  
  private String commitId;
  private String authorName;
  private String when;
  private String shortMsg;
  private String timezone;
  private String authorEmail;
  private String trimmedMsg;
  private String commitHash;
  private String isMergeCommit;
  private String repoName;
  private String branchName;
  private String commitDate;
  private String formattedTime;

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getWhen() {
    return when;
  }

  public void setWhen(String when) {
    this.when = when;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getAuthorEmail() {
    return authorEmail;
  }

  public void setAuthorEmail(String authorEmail) {
    this.authorEmail = authorEmail;
  }

  public String getShortMsg() {
    return shortMsg;
  }

  public void setShortMsg(String shortMsg) {
    this.shortMsg = shortMsg;
  }

  public String getTrimmedMsg() {
    return trimmedMsg;
  }

  public void setTrimmedMsg(String trimmedMsg) {
    this.trimmedMsg = trimmedMsg;
  }

  public String getCommitHash() {
    return commitHash;
  }

  public void setCommitHash(String commitHash) {
    this.commitHash = commitHash;
  }

  public String getIsMergeCommit() {
    return isMergeCommit;
  }

  public void setIsMergeCommit(String isMergeCommit) {
    this.isMergeCommit = isMergeCommit;
  }

  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String reponame) {
    this.repoName = reponame;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getCommitDate() {
    return commitDate;
  }

  public void setCommitDate(String commitDate) {
    this.commitDate = commitDate;
  }

  public String getFormattedTime() {
    return formattedTime;
  }

  public void setFormattedTime(String formattedTime) {
    this.formattedTime = formattedTime;
  }
}

