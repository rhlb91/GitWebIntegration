package com.teammerge.form;

public class CreateNewBranchForm {

  private String companyId;
  
  private String projectId;

  private String branchName;
  
  private String startingPoint;

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

  /**
   * @return the branchName
   */
  public String getBranchName() {
    return branchName;
  }

  /**
   * @param branchName the branchName to set
   */
  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getStartingPoint() {
    return startingPoint;
  }

  public void setStartingPoint(String startingPoint) {
    this.startingPoint = startingPoint;
  }


}
