package com.teammerge.validator.impl;

import java.util.Map;

import com.teammerge.Constants.WebServiceResult;
import com.teammerge.validator.BaseValidator;

public abstract class AbstractValidator<T> implements BaseValidator<T> {
  public void putErrorsInMap(Map<String, Object> result, ValidationResult vr) {
    result.put("result", WebServiceResult.VALIDATION_ERROR);
    
    String errors = "";
    for (FieldError e : vr.getErrors()) {
      errors += "[" + e.fieldName + "]-[" + e.fieldError + "]";
    }
    result.put("reason", errors);
  }
}
