package com.teammerge.dao.impl;

import org.springframework.stereotype.Repository;

import com.teammerge.dao.CompanyDao;
import com.teammerge.entity.Company;

@Repository("companyDao")
public class CompanyDaoImpl extends BaseDaoImpl<Company> implements CompanyDao {

  @Override
  public void setClazz(Class<Company> clazz) {
    super.setClazz(clazz);
  }
}
