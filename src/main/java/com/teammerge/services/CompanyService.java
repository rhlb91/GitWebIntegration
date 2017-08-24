package com.teammerge.services;

import com.teammerge.Constants.CloneStatus.RepoActiveStatus;
import com.teammerge.entity.Company;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.RepoForm;


public interface CompanyService {

  Company getCompanyDetails(String name);

  void saveCompanyDetails(Company name);

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

  void saveCompanyDetails(final CompanyForm companyForm);

  /**
   * gets the repository status of the provided repository and company.
   * 
   * @see RepoActiveStatus
   * 
   * @param companyId
   * @param projectId
   * @return
   */
  RepoActiveStatus getStatusForRepo(Company company, String repositoryName);

  /**
   * sets the repository status of the provided repository and company.
   * 
   * @see RepoActiveStatus
   * 
   * @param companyId
   * @param projectId
   * @return
   */
  void setRepoStatusButNoSave(String companyId, String projectId, String status);

  /**
   * checks is the repository status of the provided repository and company valid or not.
   * 
   * 
   * @see RepoActiveStatus
   * 
   * @param companyId
   * @param projectId
   * @return true iff repository status is ACTIVE in DB, else false
   */
  boolean isRepoStatusValidForWorking(String companyId, String repositoryName);


}
