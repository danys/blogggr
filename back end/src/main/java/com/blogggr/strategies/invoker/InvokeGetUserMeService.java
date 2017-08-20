package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 21.04.17.
 */
public class InvokeGetUserMeService implements ServiceInvocationStrategy {

  private UserService userService;

  public InvokeGetUserMeService(UserService userService) {
    this.userService = userService;
  }

  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException {
    User user = userService.getUserById(userID);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    //Filter out unwanted fields
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(user, FilterFactory.getUserFilter());
    return node;
  }
}