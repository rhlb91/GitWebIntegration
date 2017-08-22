package com.teammerge.validator.impl;


import com.teammerge.form.BranchForm;
import com.teammerge.utils.StringUtils;


public class BranchValidator extends AbstractValidator<BranchForm> {

  @Override
  public void validate(BranchForm form, ValidationResult e) {

    if (StringUtils.isEmpty(form.getBranchId())) {
      e.addError("branchId", "BranchId cannot be null or empty");
    }

    if (StringUtils.isEmpty(form.getShortName())) {
      e.addError("shortName", "ShortName cannot be null or empty");
    }

    if (StringUtils.isEmpty(form.getRepositoryId())) {
      e.addError("repositoryId", "RepositoryId cannot be null or empty");
    }

    if (form.getNumOfCommits() < 0) {
      e.addError("numOfCommits", "Num of commits should not be less than 0");
    }

    if (form.getNumOfPull() < 0) {
      e.addError("numOfPull", "Num of pull should not be less than 0");
    }

  }
}
