package com.teammerge.form;

public class CommitTreeRequestForm {

  private String projectId;

  private String commitId;

  private String path;


  public CommitTreeRequestForm(String projectId, String commitId, String path) {
    this.projectId = projectId;
    this.commitId = commitId;
    this.path = path;
  }

  /**
   * @return the projectId
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

}
