package com.teammerge.services;

import com.teammerge.entity.Company;
import com.teammerge.form.RepoForm;


public interface CompanyService {

  Company getCompanyDetails(String name);

  int saveCompanyDetails(Company name);

  void saveOrUpdateCompanyDetails(final RepoForm repoForm);

  String getRemoteUrlForCompanyAndProject(final String companyId, final String projectId);

  Company getCompanyForProject(String projectId);

  /**
   * returns the remote url for the specified project Id.
   * 
   * <br>
   * <br>
   * Since we are not passing companyId in this function, thus the first non empty remoteUrl will be
   * returned
   * 
   * @param projectId projectId/projectName
   * @return remoteUrl
   */
  String getRemoteUrlForProject(String projectId);

  String getRemoteUrlForCompanyAndProject(final Company company, final String projectId);

}
