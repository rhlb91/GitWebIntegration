package com.teammerge.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.entity.Company;
import com.teammerge.form.RepoForm;
import com.teammerge.services.CompanyDetailService;
import com.teammerge.utils.StringUtils;

@Service("companyDetailService")
public class CompanyDetailServiceImpl implements CompanyDetailService {

  @Autowired
  private BaseDao<Company> baseDao;

  @Override
  public Company getCompanyDetails(String name) {
    Company company = baseDao.fetchEntity(name);
    return company;
  }

  @Override
  public int saveCompanyDetails(Company name) {
    baseDao.saveEntity(name);
    return 0;
  }

  @Autowired
  public void setBaseDao(BaseDao<Company> baseDao) {
    baseDao.setClazz(Company.class);
    this.baseDao = baseDao;
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

    saveCompanyDetails(company);
  }

  public String getRemoteUrlForCompanyAndProject(final String companyId, final String projectId) {
    Company c = baseDao.fetchEntity(companyId);

    if (c == null || StringUtils.isEmpty(companyId) || StringUtils.isEmpty(projectId)) {
      return null;
    }

    for (String s : c.getRemoteRepoUrls().keySet()) {
      if (projectId.equals(s)) {
        return c.getRemoteRepoUrls().get(s);
      }
    }

    return null;
  }
}
