package com.teammerge.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

public interface BaseValidator<T> {
  void validate(T t, ValidationResult e);

  ValidationResult validate(T t);

  class ValidationResult {

    protected List<FieldError> errors;

    public ValidationResult() {}

    public boolean hasErrors() {
      if (!CollectionUtils.isEmpty(errors)) {
        return false;
      }
      return true;
    }

    public void addError(String fieldName, String fieldError) {
      if (CollectionUtils.isEmpty(errors)) {
        errors = new ArrayList<>();
      }

      FieldError e = new FieldError(fieldName, fieldError);
      errors.add(e);
    }

    public List<FieldError> getErrors() {
      return errors;
    }
  }


  public class FieldError {
    public String fieldName;
    public String fieldError;

    public FieldError(String fieldName, String fieldError) {
      this.fieldName = fieldName;
      this.fieldError = fieldError;
    }
  }
}
