package com.teammerge.validator.impl;

import com.teammerge.form.CommitDiffRequestForm;
import com.teammerge.utils.StringUtils;


public class CommitDiffValidator extends AbstractValidator<CommitDiffRequestForm> {

  @Override
  public void validate(CommitDiffRequestForm form, ValidationResult e) {
    if (StringUtils.isEmpty(form.getCommitId())) {
      e.addError("commitId", "CommitId cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getRepositoryName())) {
      e.addError("repositoryName", "Repository Name cannot be null or empty");
    }
  }
}
