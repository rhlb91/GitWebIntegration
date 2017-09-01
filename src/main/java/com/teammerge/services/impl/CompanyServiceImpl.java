package com.teammerge.services.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.Constants.CloneStatus.RepoActiveStatus;
import com.teammerge.dao.CompanyDao;
import com.teammerge.entity.Company;
import com.teammerge.entity.CompanyKey;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.RepoForm;
import com.teammerge.populator.CompanyPopulator;
import com.teammerge.services.CompanyService;
import com.teammerge.utils.StringUtils;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService {

  private final static Logger LOG = LoggerFactory.getLogger(CompanyServiceImpl.class);

  private CompanyDao companyDao;

  @Resource(name = "companyPopulator")
  private CompanyPopulator companyPopulator;


  @Override
  public Company getCompanyDetails(String cName, String pName) {
    CompanyKey key = new CompanyKey(cName, pName);
    Company company = companyDao.fetchEntity(key);
    return company;
  }

  @Override
  public List<Company> getCompanyDetailsForName(String cName) {
    return companyDao.fetchEntityForName(cName);
  }

  @Override
  public void saveCompanyDetails(Company company) {
    Company existingCompany = getCompanyDetails(company.getName(), company.getProjectName());

    if (existingCompany == null) {
      existingCompany = company;
    } else {
      companyPopulator.populate(company, existingCompany);
    }
    companyDao.saveOrUpdateEntity(existingCompany);
  }

  @Override
  public void saveCompanyDetails(CompanyForm companyForm) {
    Company company = new Company();
    companyPopulator.populate(companyForm, company);
    saveCompanyDetails(company);
  }

  public void saveOrUpdateCompanyDetails(final RepoForm repoForm) {
    Company company = getCompanyDetails(repoForm.getCompanyName(), repoForm.getProjectName());


    if (company == null) {
      company = new Company();
      companyPopulator.populate(repoForm, company);
    } else {
      company.setRemoteURL(repoForm.getRepoRemoteURL());
      company.setStatus(RepoActiveStatus.ACTIVE.toString());
    }

    companyDao.saveOrUpdateEntity(company);
  }

  // private boolean checkIsCompanyRepoActiveStatus(String cName,String repoName) {
  // List<Company> companies = companyDao.fetchAll();
  //
  // if (CollectionUtils.isEmpty(companies)) {
  // // if there is no entry then probably this is the first time when application has ran,
  // // thus created a initial status model and saving
  // Company newRepoStatusModel = new Company(repoName);
  // companyDao.saveEntity(newRepoStatusModel);
  //
  // LOG.error("No Repo status entry found in DB!! Added one for " + repoName);
  // return false;
  // }
  // return true;
  // }


  public String getRemoteUrlForCompanyAndProject(final String companyId, final String projectId) {
    if (StringUtils.isEmpty(companyId) || StringUtils.isEmpty(projectId)) {
      LOG.error("Cannot get remoteUrl companyId and projectId are null!!");
      return null;
    }

    Company c = companyDao.fetchEntity(companyId);
    return getRemoteUrlForCompanyAndProject(c, projectId);
  }

  @Override
  public String getRemoteUrlForCompanyAndProject(final Company company, final String projectId) {
    Company c = company;

    if (c == null || StringUtils.isEmpty(projectId)) {
      LOG.error("Cannot get remoteUrl, company or projectId is null!!");
      return null;
    }

    return c.getRemoteURL();
  }

  @Override
  public Company getCompanyForProject(String projectId) {
    Company resultCompany = null;
    List<Company> companies = companyDao.fetchAll();

    if (CollectionUtils.isEmpty(companies)) {
      return null;
    }

    for (Company c : companies) {
      if (projectId.equalsIgnoreCase(c.getProjectName())) {
        resultCompany = c;
        break;
      }
    }
    return resultCompany;
  }


  @Override
  public RepoActiveStatus getStatusForRepo(final Company company, final String repositoryName) {
    String repoStatus = company.getStatus();

    if (repositoryName.equals(company.getProjectName())) {
      return RepoActiveStatus.forName(repoStatus);
    }
    return null;
  }

  @Override
  public void setRepoStatus(final String companyId, final String repositoryName, final String status) {
    Company company = getCompanyDetails(companyId, repositoryName);
    company.setStatus(status);
    companyDao.saveOrUpdateEntity(company);
  }

  @Override
  public boolean isRepoStatusValidForWorking(final String companyId, final String repoName) {
    Company company = getCompanyDetails(companyId, repoName);

    RepoActiveStatus status = RepoActiveStatus.forName(company.getStatus());
    if (RepoActiveStatus.ACTIVE.equals(status)) {
      return true;
    }
    return false;
  }

  @Autowired
  public void setCompanyDao(CompanyDao companyDao) {
    companyDao.setClazz(Company.class);
    this.companyDao = companyDao;
  }
}
