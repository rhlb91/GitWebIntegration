package com.teammerge.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "branch_last_commit_added")
public class BranchLastCommitAdded implements Serializable {
  private static final long serialVersionUID = 1359951173392913330L;

  @Id
  @Column(name = "branch_id")
  private String branchId;

  @Column(name = "last_modified")
  private Date lastCommitDate;

  public BranchLastCommitAdded() {
    super();
  }

  public BranchLastCommitAdded(String branchId, Date lastCommitDate) {
    super();
    this.branchId = branchId;
    this.lastCommitDate = lastCommitDate;
  }

  public Date getLastModified() {
    return lastCommitDate;
  }

  public void setLastModified(Date lastModified) {
    this.lastCommitDate = lastModified;
  }

}
