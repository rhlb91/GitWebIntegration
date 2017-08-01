package com.teammerge.validator.impl;

import com.teammerge.form.CommitDiffRequestForm;
import com.teammerge.utils.StringUtils;
import com.teammerge.validator.BaseValidator;


public class CommitDiffValidator implements BaseValidator<CommitDiffRequestForm> {

  @Override
  public com.teammerge.validator.BaseValidator.ValidationResult validate(CommitDiffRequestForm form) {

    ValidationResult e = new ValidationResult();
    validate(form, e);
    return e;
  }

  @Override
  public void validate(CommitDiffRequestForm form, com.teammerge.validator.BaseValidator.ValidationResult e) {
//    if (StringUtils.isEmpty(form.getBranchName())) {
//      e.addError("branchName", "Branch cannot be null or empty");
//    }

    if (StringUtils.isEmpty(form.getCommitId())) {
      e.addError("commitId", "CommitId cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getRepositoryName())) {
      e.addError("repositoryName", "Repository Name cannot be null or empty");
    }
  }
}
