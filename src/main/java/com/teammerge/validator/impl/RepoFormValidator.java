package com.teammerge.validator.impl;


import com.teammerge.form.RepoForm;
import com.teammerge.utils.StringUtils;


public class RepoFormValidator extends AbstractValidator<RepoForm> {

  @Override
  public ValidationResult validate(RepoForm form) {

    ValidationResult e = new ValidationResult();
    validate(form, e);
    return e;
  }

  @Override
  public void validate(RepoForm form, ValidationResult e) {

    if (StringUtils.isEmpty(form.getCompanyName())) {
      e.addError("CompanyName", "CompanyName cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getProjectName())) {
      e.addError("{ProjectName", "ProjectName cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getRepoRemoteURL())) {
      e.addError("{repoRemoteURL", "Repository Remote URL cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getUsername())) {
      e.addError("{userName", "User name cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getPassword())) {
      e.addError("{password", "Password cannot be null or empty");
    }
  }
}
