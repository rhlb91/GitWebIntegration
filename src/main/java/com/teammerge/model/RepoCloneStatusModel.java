package com.teammerge.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.teammerge.Constants.CloneStatus;

@Entity
@Table(name = "repo_clone_status")
public class RepoCloneStatusModel implements Serializable{
  private static final long serialVersionUID = -6976452034587151083L;

  @Id
  @Column(name = "repo_name")
  private String repoName;

  @Column(name = "clone_status")
  private String cloneStatus;

  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  public String getCloneStatus() {
    return cloneStatus;
  }

  public void setCloneStatus(String cloneStatus) {
    this.cloneStatus = cloneStatus;
  }

}
