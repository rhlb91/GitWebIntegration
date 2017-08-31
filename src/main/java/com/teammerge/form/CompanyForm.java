package com.teammerge.form;


public class CompanyForm {

  private String name;

  private String remoteRepoUrl;

  private String projectName;

  private String isRepoActive;

  public String getRemoteRepoUrl() {
    return remoteRepoUrl;
  }

  public void setRemoteRepoUrl(String remoteRepoUrl) {
    this.remoteRepoUrl = remoteRepoUrl;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * @return the Company Name
   */
  public String getName() {
    return name;
  }

  /**
   * @param Company Name
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getIsRepoActive() {
    return isRepoActive;
  }

  public void setIsRepoActive(String isRepoActive) {
    this.isRepoActive = isRepoActive;
  }

}
