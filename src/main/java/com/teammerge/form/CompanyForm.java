package com.teammerge.form;

import java.util.Map;

public class CompanyForm {

  private String name;
  
  private Map<String, String> remoteRepoUrls;

  
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

  public Map<String, String> getRemoteRepoUrls() {
    return remoteRepoUrls;
  }

  public void setRemoteRepoUrls(Map<String, String> remoteRepoUrls) {
    this.remoteRepoUrls = remoteRepoUrls;
  }

  

 }
