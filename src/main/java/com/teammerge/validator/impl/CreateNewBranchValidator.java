package com.teammerge.validator.impl;


import com.teammerge.form.CreateNewBranchForm;
import com.teammerge.utils.StringUtils;


public class CreateNewBranchValidator extends AbstractValidator<CreateNewBranchForm> {

  @Override
  public void validate(CreateNewBranchForm form, ValidationResult e) {

    if (StringUtils.isEmpty(form.getBranchName())) {
      e.addError("BranchName", "BranchName cannot be null or empty");
    }
    if (StringUtils.isEmpty(form.getCompanyId())) {
      e.addError("companyId", "companyId cannot be null or empty");
    }
  }
}
