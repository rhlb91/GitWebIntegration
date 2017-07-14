package com.teammerge.dao.impl;

import com.teammerge.Keys.execution;
import com.teammerge.dao.CompanyDetailDao;
import com.teammerge.entity.Company;
import com.teammerge.utils.HibernateUtils;

public class CompanyDetailDaoImpl implements CompanyDetailDao{

  @Override
  public Company getCompany(String name) {

    HibernateUtils.openCurrentSessionwithTransaction();
    Company company = (Company) HibernateUtils.getCurrentSession().get(Company.class, name);
    return company;
  }

  @Override
  public void saveCompany(Company name) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().save(name);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }

}
