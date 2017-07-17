package com.teammerge.services.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.teammerge.dao.CompanyDetailDao;
import com.teammerge.entity.Company;
import com.teammerge.services.CompanyDetailService;
import com.teammerge.utils.HibernateUtils;

@Service("companyDetailService")
public class CompanyDetailServiceImpl implements CompanyDetailService {

  @Resource(name = "companyDetailDao")
  private CompanyDetailDao companyDetailDao;
  
  @Override
  public Company getCompanyDetails(String name) {
    Company company= companyDetailDao.getCompany(name);
    return company;
  }
  
  @Override
  public int saveDetails(Company name) {
    companyDetailDao.saveCompany(name);
    return 0;
  }

}
