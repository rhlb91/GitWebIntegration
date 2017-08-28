package com.teammerge.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.Constants.CloneStatus.RepoActiveStatus;
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
      // saving remote urls
      Map<String, String> remoteRepo = existingCompany.getRemoteRepoUrls();
      remoteRepo.putAll(company.getRemoteRepoUrls());
      existingCompany.setRemoteRepoUrls(remoteRepo);

      // saving repo status
      Map<String, String> existingRepoStatuses = existingCompany.getRepoStatuses();
      if (MapUtils.isEmpty(existingRepoStatuses)) {
        existingRepoStatuses = new HashMap<>();
      }

      // setting to status: Active for repositories whose status does not exists
      for (String project : existingRepoStatuses.keySet()) {
        if (StringUtils.isEmpty(existingRepoStatuses.get(project))) {
          existingRepoStatuses.put(project, RepoActiveStatus.ACTIVE.toString());
        }
      }

      Map<String, String> newRepoStatuses = company.getRepoStatuses();
      if (!MapUtils.isEmpty(newRepoStatuses)) {
        for (String project : newRepoStatuses.keySet()) {
          if (StringUtils.isEmpty(existingRepoStatuses.get(project))) {
            existingRepoStatuses.put(project, newRepoStatuses.get(project));
          }
        }
      }

      existingCompany.setRepoStatuses(existingRepoStatuses);
    }
    baseDao.saveOrUpdateEntity(existingCompany);
  }

  @Override
  public void saveCompanyDetails(CompanyForm companyForm) {
    Company company = new Company();
    companyPopulator.populate(companyForm, company);

    // populating repo status for the first time, setting to Active
    Map<String, String> newRepoStatuses = new HashMap<>();
    if (company.getRemoteRepoUrls() != null) {
      for (String project : company.getRemoteRepoUrls().keySet()) {
        newRepoStatuses.put(project, RepoActiveStatus.ACTIVE.toString());
      }
    }
    company.setRepoStatuses(newRepoStatuses);

    saveCompanyDetails(company);
  }

  public void saveOrUpdateCompanyDetails(final RepoForm repoForm) {
    Company company = getCompanyDetails(repoForm.getCompanyName());


    if (company == null) {
      company = new Company();
      company.setName(repoForm.getCompanyName());

      Map<String, String> repoStatus = new HashMap<>();
      repoStatus.put(repoForm.getProjectName(), RepoActiveStatus.ACTIVE.toString());
      company.setRepoStatuses(repoStatus);

    }

    Map<String, String> remoteRepo = company.getRemoteRepoUrls();
    remoteRepo.put(repoForm.getProjectName(), repoForm.getRepoRemoteURL());
    company.setRemoteRepoUrls(remoteRepo);

    Map<String, String> existrepoStatus = company.getRepoStatuses();
    for (String project : company.getRemoteRepoUrls().keySet()) {
      if (!StringUtils.isEmpty(project) && company.getRepoStatuses().containsValue(RepoActiveStatus.IN_ACTIVE.toString())) {
        existrepoStatus.put(project, RepoActiveStatus.ACTIVE.toString());
      }
      company.setRepoStatuses(existrepoStatus);
    }
    baseDao.saveOrUpdateEntity(company);
  }

  private boolean checkIsCompanyRepoActiveStatus(String repoName) {
    List<Company> companies = baseDao.fetchAll();

    if (CollectionUtils.isEmpty(companies)) {
      // if there is no entry then probably this is the first time when application has ran,
      // thus created a initial status model and saving
      Company newRepoStatusModel = new Company(repoName);
      baseDao.saveEntity(newRepoStatusModel);

      LOG.error("No Repo status entry found in DB!! Added one for " + repoName);
      return false;
    }
    return true;
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

  @Override
  public RepoActiveStatus getStatusForRepo(final Company company, final String repositoryName) {
    Map<String, String> repoStatuses = company.getRepoStatuses();

    for (String projectId : repoStatuses.keySet()) {
      if (projectId.equals(repositoryName)) {
        return RepoActiveStatus.forName(repoStatuses.get(projectId));
      }
    }
    return null;
  }

  @Override
  public void setRepoStatusButNoSave(final String companyId, final String repositoryName,
      final String status) {
    Company company = getCompanyDetails(companyId);
    Map<String, String> repoStatuses = company.getRepoStatuses();

    for (String projectId : repoStatuses.keySet()) {
      if (projectId.equals(repositoryName)) {
        repoStatuses.put(projectId, status);
      }
    }

    company.setRepoStatuses(repoStatuses);
    baseDao.saveOrUpdateEntity(company);
  }

  @Override
  public boolean isRepoStatusValidForWorking(final String companyId, final String repositoryName) {
    Company company = getCompanyDetails(companyId);
    Map<String, String> repoStatuses = company.getRepoStatuses();

    for (String projectId : repoStatuses.keySet()) {
      if (projectId.equals(repositoryName)) {

        RepoActiveStatus status = RepoActiveStatus.forName(repoStatuses.get(projectId));
        if (RepoActiveStatus.ACTIVE.equals(status)) {
          return true;
        }
      }
    }
    return false;

  }

  @Autowired
  public void setBaseDao(BaseDao<Company> baseDao) {
    baseDao.setClazz(Company.class);
    this.baseDao = baseDao;
  }
}
