package com.blogggr.validators;

import com.blogggr.dto.UserPostData;
import com.blogggr.dto.UserPutData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class UserPutDataValidator extends GenericValidator {

  protected boolean validate(Map<String, String> input, String body)
      throws JsonParseException, JsonProcessingException, IOException {
    //Check if id is present
    if (!input.containsKey(IdValidator.idName)) {
      errorMessage = "User id not provided!";
      return false;
    }
    if (!stringIsNumber(input.get(IdValidator.idName))) {
      errorMessage = "User must be identified by its id!";
      return false;
    }
    ObjectMapper mapper = new ObjectMapper();
    UserPutData userData = mapper.readValue(body, UserPutData.class);
    //Check the validity of the body parameters
    //If any of the password parameters is given, all other parameters must be given as well
    if (userData.getOldPassword() != null || userData.getPassword() != null
        || userData.getPasswordRepeat() != null) {
      if (userData.getOldPassword() == null || userData.getPassword() == null
          || userData.getPasswordRepeat() == null) {
        errorMessage = "Provide the old password and the new password twice!";
        return false;
      } else if (userData.getPassword().compareTo(userData.getPasswordRepeat()) != 0) {
        errorMessage = "The new password and the repeated new password do not match!";
        return false;
      }
    }
    if (userData.getFirstName() == null
        && userData.getLastName() == null
        && userData.getEmail() == null
        && userData.getPassword() == null) {
      errorMessage = "At least one field must be provided!";
      return false;
    }
    //Check other conditions
    UserPostData temp = new UserPostData();
    temp.setFirstName(userData.getFirstName());
    temp.setLastName(userData.getLastName());
    String valRes = UserPostDataValidator.validateUserData(temp);
    if (!valRes.isEmpty()) {
      errorMessage = valRes;
      return false;
    }
    return true;
  }
}
