package com.blogggr.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.01.17.
 */
public class GetUsersValidator extends GenericValidator {

  public GetUsersValidator() {
    //
  }

  public static final String SEARCH_KEY = "search";
  public static final String FIRST_NAME_SEARCH_KEY = "firstName";
  public static final String LAST_NAME_SEARCH_KEY = "lastName";
  public static final String EMAIL_SEARCH_KEY = "email";
  public static final String PAGE_KEY = "page";
  public static final String LIMIT_KEY = "limit";
  public static final String LENGTH_KEY = "length";
  public static final String BEFORE_KEY = "before";
  public static final String AFTER_KEY = "after";

  private final int MINIMUM_SEARCH_LEN = 3;

  @Override
  protected boolean validate(Map<String, String> input, String body) {
    if (input.containsKey(SEARCH_KEY)) {
      if (input.get(SEARCH_KEY).length() < MINIMUM_SEARCH_LEN) {
        errorMessage = "Search string has a minimum length of " + MINIMUM_SEARCH_LEN + "!";
        return false;
      }
    } else { //No search key => maybe has firstName, lastName and email search keys
      //No minimum key length restriction for search fields
      if (!input.containsKey(LENGTH_KEY)) {
        errorMessage = "Length parameter must be provided!";
        return false;
      }
      if (input.containsKey(LENGTH_KEY) && !stringIsNumber(input.get(LENGTH_KEY))) {
        errorMessage = "Length must be an integer number!";
        return false;
      }
      if (input.containsKey(AFTER_KEY) && input.containsKey(BEFORE_KEY)) {
        errorMessage = "Provide only after or before key but not both!";
        return false;
      }
    }
    //If no string has been provided the input is also accepted => wildcard search
    if (input.containsKey(PAGE_KEY) && !stringIsNumber(input.get(PAGE_KEY))) {
      errorMessage = "Page parameter is not a valid number!";
      return false;
    }
    if (input.containsKey(LIMIT_KEY) && !stringIsNumber(input.get(LIMIT_KEY))) {
      errorMessage = "Limit parameter is not a valid number!";
      return false;
    }
    return true;
  }
}
