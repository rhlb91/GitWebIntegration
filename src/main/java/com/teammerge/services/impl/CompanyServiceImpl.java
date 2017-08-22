package com.teammerge.services.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.entity.Company;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.RepoForm;
import com.teammerge.populator.CompanyPopulator;
import com.teammerge.services.CompanyService;
import com.teammerge.utils.StringUtils;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService {

  private final static Logger LOG = LoggerFactory.getLogger(CompanyServiceImpl.class);

  private BaseDao<Company> baseDao;

  @Resource(name = "companyPopulator")
  private CompanyPopulator companyPopulator;

  @Override
  public Company getCompanyDetails(String name) {
    Company company = baseDao.fetchEntity(name);
    return company;
  }

  @Override
  public void saveCompanyDetails(Company company) {
    Company existingCompany = getCompanyDetails(company.getName());

    if (existingCompany == null) {
      existingCompany = company;
    } else {
      Map<String, String> remoteRepo = existingCompany.getRemoteRepoUrls();
      remoteRepo.putAll(company.getRemoteRepoUrls());
      existingCompany.setRemoteRepoUrls(remoteRepo);
    }
    baseDao.saveEntity(existingCompany);
  }

  @Override
  public void saveCompanyDetails(CompanyForm companyForm) {
    Company company = new Company();
    companyPopulator.populate(companyForm, company);
    saveCompanyDetails(company);
  }

  public void saveOrUpdateCompanyDetails(final RepoForm repoForm) {
    Company company = getCompanyDetails(repoForm.getCompanyName());

    if (company == null) {
      company = new Company();
      company.setName(repoForm.getCompanyName());
    }

    Map<String, String> remoteRepo = company.getRemoteRepoUrls();
    remoteRepo.put(repoForm.getProjectName(), repoForm.getRepoRemoteURL());
    company.setRemoteRepoUrls(remoteRepo);

    baseDao.saveEntity(company);
  }

  public String getRemoteUrlForCompanyAndProject(final String companyId, final String projectId) {
    if (StringUtils.isEmpty(companyId) || StringUtils.isEmpty(projectId)) {
      LOG.error("Cannot get remoteUrl companyId and projectId are null!!");
      return null;
    }

    Company c = baseDao.fetchEntity(companyId);
    return getRemoteUrlForCompanyAndProject(c, projectId);
  }

  @Override
  public String getRemoteUrlForCompanyAndProject(final Company company, final String projectId) {
    Company c = company;

    if (c == null || StringUtils.isEmpty(projectId)) {
      LOG.error("Cannot get remoteUrl, company or projectId is null!!");
      return null;
    }

    for (String s : c.getRemoteRepoUrls().keySet()) {
      if (projectId.equals(s)) {
        return c.getRemoteRepoUrls().get(s);
      }
    }

    return null;
  }

  @Override
  public Company getCompanyForProject(String projectId) {
    Company resultCompany = null;
    List<Company> companies = baseDao.fetchAll();

    if (CollectionUtils.isEmpty(companies)) {
      return null;
    }

    for (Company c : companies) {
      if (MapUtils.isNotEmpty(c.getRemoteRepoUrls())) {

        for (String pId : c.getRemoteRepoUrls().keySet()) {
          if (projectId.equals(pId)) {
            resultCompany = c;
            break;
          }
        }

      }
    }
    return resultCompany;
  }

  @Override
  public String getRemoteUrlForProject(String projectId) {
    String remoteUrl = null;
    List<Company> companies = baseDao.fetchAll();

    if (CollectionUtils.isEmpty(companies)) {
      return null;
    }

    for (Company c : companies) {
      if (MapUtils.isNotEmpty(c.getRemoteRepoUrls())) {
        remoteUrl = c.getRemoteRepoUrls().get(projectId);
        if (!StringUtils.isEmpty(remoteUrl)) {
          break;
        }
      }
    }
    return remoteUrl;
  }

  @Autowired
  public void setBaseDao(BaseDao<Company> baseDao) {
    baseDao.setClazz(Company.class);
    this.baseDao = baseDao;
  }
}
