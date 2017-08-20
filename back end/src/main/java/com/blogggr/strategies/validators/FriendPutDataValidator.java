package com.blogggr.strategies.validators;

import com.blogggr.requestdata.FriendData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class FriendPutDataValidator extends GenericValidator {

  public static final String idName = "id";
  public static final String id2Name = "id2";

  protected boolean validate(Map<String, String> input, String body)
      throws JsonParseException, JsonProcessingException, IOException {
    if (!input.containsKey(idName) && !input.containsKey(id2Name)) {
      errorMessage = "Error getting resource ids!";
      return false;
    }
    if (!stringIsNumber(input.get(idName)) || !stringIsNumber(input.get(id2Name))) {
      errorMessage = "Resource id not a valid number!";
      return false;
    }
    ObjectMapper mapper = new ObjectMapper();
    FriendData friendData = mapper.readValue(body, FriendData.class);
    //Check that all fields are present
    if (friendData.getAction() == null) {
      errorMessage = "Action field must be filled!";
      return false;
    }
    //Check if action is a number and has a value of 0, 1, 2 or 3.
    int actionVal = friendData.getAction().intValue();
    if (actionVal <= 0 || actionVal > 3) {
      errorMessage = "Action number must be 1, 2 or 3!";
      return false;
    }
    return true;
  }
}

