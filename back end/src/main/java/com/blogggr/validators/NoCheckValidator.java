package com.blogggr.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class NoCheckValidator extends GenericValidator {

  protected boolean validate(Map<String, String> input, String body) {
    return true;
  }
}