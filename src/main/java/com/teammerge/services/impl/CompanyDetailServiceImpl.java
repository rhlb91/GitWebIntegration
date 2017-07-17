package com.teammerge.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.entity.Company;
import com.teammerge.model.BranchDetailModel;
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
  public int saveDetails(Company name) {
    baseDao.saveEntity(name);
    return 0;
  }

  @Autowired
  public void setBaseDao(BaseDao<Company> baseDao) {
    baseDao.setClazz(Company.class);
    this.baseDao = baseDao;
  }

}
