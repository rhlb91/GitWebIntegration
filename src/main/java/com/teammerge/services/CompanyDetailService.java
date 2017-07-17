package com.teammerge.services;

import com.teammerge.entity.Company;
import com.teammerge.form.RepoForm;


public interface CompanyDetailService {

  Company getCompanyDetails(String name);

  int saveCompanyDetails(Company name);

  void saveOrUpdateCompanyDetails(final RepoForm repoForm);

}
