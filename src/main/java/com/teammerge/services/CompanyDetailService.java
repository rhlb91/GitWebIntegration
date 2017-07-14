package com.teammerge.services;

import com.teammerge.entity.Company;


public interface CompanyDetailService {
  
  public Company getCompanyDetails(String name);

  public int saveDetails(Company name);

}
