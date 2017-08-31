package com.teammerge.services;

import java.util.List;

import com.teammerge.Constants.CloneStatus.RepoActiveStatus;
import com.teammerge.entity.Company;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.RepoForm;


public interface CompanyService {

  void saveCompanyDetails(Company name);

  void saveOrUpdateCompanyDetails(final RepoForm repoForm);

  String getRemoteUrlForCompanyAndProject(final String companyId, final String projectId);

  /**
   * <p>
   * As in this project, we are treating project Name to be unique.
   * </p>
   * <p>
   * If there are more than 1 company with same project, the first one will be returned
   * </p>
   * 
   * @param projectId
   * @return company
   */
  Company getCompanyForProject(String projectId);

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
  void setRepoStatus(String companyId, String projectId, String status);

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

  Company getCompanyDetails(String cName, String pName);

  /**
   * gets all the companies with the same name
   * 
   * @param cName
   * @return
   */
  List<Company> getCompanyDetailsForName(String cName);


}
