package com.teammerge.validator.impl;


import com.teammerge.form.CommitForm;
import com.teammerge.form.CreateNewBranchForm;
import com.teammerge.utils.StringUtils;


public class CommitFormValidator extends AbstractValidator<CommitForm> {

  @Override
  public ValidationResult validate(CommitForm form) {

    ValidationResult e = new ValidationResult();
    validate(form, e);
    return e;
  }

  @Override
  public void validate(CommitForm form, ValidationResult e) {

    if (StringUtils.isEmpty(form.getBranchName())) {
      e.addError("branchName", "branchName cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getCommitId())) {
      e.addError("commitId", "commitId cannot be null or empty");
    }
  }
}
