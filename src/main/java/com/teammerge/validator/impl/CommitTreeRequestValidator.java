package com.teammerge.validator.impl;

import com.teammerge.form.CommitTreeRequestForm;
import com.teammerge.utils.StringUtils;

public class CommitTreeRequestValidator extends AbstractValidator<CommitTreeRequestForm> {

  @Override
  public void validate(CommitTreeRequestForm form, ValidationResult e) {
    if (StringUtils.isEmpty(form.getCommitId())) {
      e.addError("commitId", "Commit Id cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getProjectId())) {
      e.addError("ProjectName", "Project Name cannot be null or empty");
    }
  }

  @Override
  public ValidationResult validate(CommitTreeRequestForm form) {
    ValidationResult e = new ValidationResult();
    validate(form, e);
    return e;
  }

}
