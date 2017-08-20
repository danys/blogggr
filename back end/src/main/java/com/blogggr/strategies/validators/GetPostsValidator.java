package com.blogggr.strategies.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.12.16.
 */
public class GetPostsValidator extends GenericValidator {

  public GetPostsValidator() {
    //nothing to do
  }

  public static final String titleKey = "title";
  public static final String posterUserIDKey = "posterUserId";
  public static final String visibilityKey = "visibility"; //can be "onlyGlobal", "all", "onlyFriends", "onlyCurrentUser"
  public static final String beforeKey = "before";
  public static final String afterKey = "after";
  public static final String limitKey = "limit";

  @Override
  protected boolean validate(Map<String, String> input, String body) {
    String idStr = null;
    Long id = null;
    if (input.containsKey(posterUserIDKey) && (!stringIsNumber(input.get(posterUserIDKey)))) {
      //Not a valid number
      errorMessage = "Poster user ID not a valid number!";
      return false;
    }
    //Check if titleKey is present
    String title = null;
    if (input.containsKey(titleKey)) {
      title = input.get(titleKey);
      if (title.length() < 3 || title.length() > 100) {
        errorMessage = "Title have a length between 3 and 100 characters!";
        return false;
      }
    }
    //Check the visibility setting
    String visibility = null;
    if (input.containsKey(visibilityKey)) {
      visibility = input.get(visibilityKey);
      if ((visibility != null) &&
          (visibility.compareTo("onlyGlobal") != 0) &&
          (visibility.compareTo("all") != 0) &&
          (visibility.compareTo("onlyFriends") != 0) &&
          (visibility.compareTo("onlyCurrentUser") != 0)) {
        errorMessage = "If provided visibility must be either of: 'onlyGlobal', 'all', 'onlyFriends', 'onlyCurrentUser'!";
        return false;
      }
    }
    //Check before post ID number
    if (input.containsKey(beforeKey) && (!stringIsNumber(input.get(beforeKey)))) {
      errorMessage = "Before parameter must be a valid number!";
      return false;
    }
    //Check after post ID number
    if (input.containsKey(afterKey) && (!stringIsNumber(input.get(afterKey)))) {
      //Not a valid number
      errorMessage = "After parameter must be a valid number!";
      return false;
    }
    //Check limit number
    if (input.containsKey(limitKey) && (!stringIsNumber(input.get(limitKey)))) {
      //Not a valid number
      errorMessage = "Limit parameter must be a valid number!";
      return false;
    }
    return true;
  }
}
