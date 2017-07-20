package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.entity.Company;
import com.teammerge.form.RepoForm;
import com.teammerge.services.CompanyDetailService;

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

    List<String> ownedRepo = company.getOwnedRepositories();
    if (CollectionUtils.isEmpty(ownedRepo)) {
      ownedRepo = new ArrayList<>();
    }

    if (!ownedRepo.contains(repoForm.getRepoName())) {
      ownedRepo.add(repoForm.getRepoName());
    }
    company.setOwnedRepositories(ownedRepo);

    Map<String, String> remoteRepo = company.getRemoteRepoUrls();
    remoteRepo.put(repoForm.getRepoName(), repoForm.getRepoRemoteURL());
    company.setRemoteRepoUrls(remoteRepo);

    saveCompanyDetails(company);

  }
}
