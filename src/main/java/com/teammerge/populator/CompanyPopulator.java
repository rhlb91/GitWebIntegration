package com.teammerge.populator;

import org.springframework.stereotype.Component;

import com.teammerge.Constants.CloneStatus.RepoActiveStatus;
import com.teammerge.entity.Company;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.RepoForm;


@Component
public class CompanyPopulator {

  public void populate(CompanyForm source, Company target) {

    target.setName(source.getName());
    target.setProjectName(source.getProjectName());
    target.setRemoteURL(source.getRemoteRepoUrl());

    RepoActiveStatus status = null;
    if (Boolean.valueOf(source.getIsRepoActive())) {
      status = RepoActiveStatus.ACTIVE;
    } else {
      status = RepoActiveStatus.IN_ACTIVE;
    }
    target.setStatus(status.toString());
  }

  public void populate(RepoForm source, Company target) {

    target.setName(source.getCompanyName());
    target.setProjectName(source.getProjectName());
    target.setStatus(RepoActiveStatus.ACTIVE.toString());
    target.setRemoteURL(source.getRepoRemoteURL());
  }

}
