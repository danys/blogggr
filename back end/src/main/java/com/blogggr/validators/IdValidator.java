package com.blogggr.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class IdValidator extends GenericValidator {

  public static final String idName = "id";

  public IdValidator() {
    //
  }

  @Override
  protected boolean validate(Map<String, String> input, String body) {
    if (!input.containsKey(idName)) {
      errorMessage = "Error getting resource id!";
      return false;
    }
    if (!stringIsNumber(input.get(idName))) {
      errorMessage = "Resource id not a valid number!";
      return false;
    }
    errorMessage = "";
    return true;
  }

}
