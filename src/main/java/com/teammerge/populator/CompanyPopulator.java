package com.teammerge.populator;

import org.springframework.stereotype.Component;

import com.teammerge.entity.Company;
import com.teammerge.form.CompanyForm;


@Component
public class CompanyPopulator {

  public void populate(CompanyForm source, Company target) {

    target.setName(source.getName());
    target.setRemoteRepoUrls(source.getRemoteRepoUrls());

  }
}
