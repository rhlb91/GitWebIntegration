package com.teammerge.model;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class CustomTicketModel {
private Integer NumOfBranches;
private Integer NumOfCommits;
public String getByAuthor() {
  return byAuthor;
}

public void setByAuthor(String byAuthor) {
  this.byAuthor = byAuthor;
}
private String ticketId;
private List<CommitModel> commits;
private String repositoryName;
private String byAuthor;

@Override
public String toString() {
  String str = "";
  str += "Repo Name: " + repositoryName;
  str += ", by Author: " + byAuthor;
  str += ", ticket Id:" + ticketId;
  if (CollectionUtils.isNotEmpty(commits)) {
    str += ", commits: " + commits;
  }
  str += "<br><br>";
  return str;
}

public Integer getNumOfBranches() {
  return NumOfBranches;
}
public void setNumOfBranches(Integer numOfBranches) {
  NumOfBranches = numOfBranches;
}
public Integer getNumOfCommits() {
  return NumOfCommits;
}
public void setNumOfCommits(Integer numOfCommits) {
  NumOfCommits = numOfCommits;
}
public String getTicketId() {
  return ticketId;
}
public void setTicketId(String ticketId) {
  this.ticketId = ticketId;
}
public List<CommitModel> getCommits() {
  return commits;
}
public void setCommits(List<CommitModel> commits) {
  this.commits = commits;
}
public String getRepositoryName() {
  return repositoryName;
}
public void setRepositoryName(String repositoryName) {
  this.repositoryName = repositoryName;
}
}
