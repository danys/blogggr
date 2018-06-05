package com.blogggr.strategies.validators;

import com.blogggr.dto.FriendData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class FriendPostDataValidator extends GenericValidator {

  protected boolean validate(Map<String, String> input, String body)
      throws JsonParseException, JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    FriendData friendData = mapper.readValue(body, FriendData.class);
    //Check that all fields are present
    if (friendData.getUserId1() == null || friendData.getUserId2() == null) {
      errorMessage = "All fields need to be filled!";
      return false;
    }
    return true;
  }
}