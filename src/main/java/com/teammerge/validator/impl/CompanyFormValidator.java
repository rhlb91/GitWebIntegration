package com.teammerge.validator.impl;


import com.teammerge.form.CompanyForm;
import com.teammerge.utils.StringUtils;


public class CompanyFormValidator extends AbstractValidator<CompanyForm> {

  @Override
  public void validate(CompanyForm form, ValidationResult e) {
   
    if (StringUtils.isEmpty(form.getName())) {
      e.addError("name", "Company cannot be null or empty");
    }
            
  }
}
