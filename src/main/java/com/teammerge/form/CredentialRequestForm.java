package com.teammerge.form;

public class CredentialRequestForm {
  private String companyId;
  private String projectId;

  public String getCompanyId() {
    return companyId;
  }

  
  public CredentialRequestForm(String companyId, String projectId) {
    super();
    this.companyId = companyId;
    this.projectId = projectId;
  }


  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }


}
