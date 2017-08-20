package com.blogggr.strategies.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.05.17.
 */
public class GetPostByLabelValidator extends GenericValidator {

  public GetPostByLabelValidator() {
    //nothing to do
  }

  public static final String userIDKey = "userID";
  public static final String postShortNameKey = "postShortName";
  public static final int minShortNameLength = 3;

  @Override
  protected boolean validate(Map<String, String> input, String body) {
    if (!input.containsKey(userIDKey) || !input.containsKey(postShortNameKey)) {
      return false;
    }
    if (!stringIsNumber(input.get(userIDKey))) {
      return false;
    }
    if (input.get(postShortNameKey).length() < minShortNameLength) {
      return false;
    }
    return true;
  }
}
