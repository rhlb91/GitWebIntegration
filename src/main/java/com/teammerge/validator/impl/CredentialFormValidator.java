package com.teammerge.validator.impl;


import com.teammerge.form.CredentialRequestForm;
import com.teammerge.utils.StringUtils;


public class CredentialFormValidator extends AbstractValidator<CredentialRequestForm> {

  @Override
  public ValidationResult validate(CredentialRequestForm form) {

    ValidationResult e = new ValidationResult();
    validate(form, e);
    return e;
  }

  @Override
  public void validate(CredentialRequestForm form, ValidationResult e) {

    if (StringUtils.isEmpty(form.getCompanyId())) {
      e.addError("companyId", "Company Id cannot be null or empty");
    }
   
    if (StringUtils.isEmpty(form.getProjectId())) {
      e.addError("{projectId", "Project Id cannot be null or empty");
    }
    
  }
}
