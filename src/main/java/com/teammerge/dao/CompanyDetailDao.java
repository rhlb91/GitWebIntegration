package com.teammerge.dao;

import com.teammerge.entity.Company;

public interface CompanyDetailDao {
  
  public Company getCompany(String name);
  
  public void saveCompany(Company branch);

}
