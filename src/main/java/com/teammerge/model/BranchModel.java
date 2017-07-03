package com.teammerge.model;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;

public class BranchModel{
 
  private ObjectId branchId;
  

  public ObjectId getBranchId() {
    return branchId;
  }

  public void setBranchId(ObjectId branchId) {
    this.branchId = branchId;
  }

  private String commitMessage;
  private List<CommitModel> commits;
  
  @Override
  public String toString() {
    String str = "";
    
    str += ", branch Id:" + branchId;
    
    str += "<br><br>";
    return str;
  }

  public String getCommitMessage() {
    return commitMessage;
  }

  public void setCommitMessage(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  public List<CommitModel> getCommits() {
    return commits;
  }

  public void setCommits(List<CommitModel> commits) {
    this.commits = commits;
  }
}