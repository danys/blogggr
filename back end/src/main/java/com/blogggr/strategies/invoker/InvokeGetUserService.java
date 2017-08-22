package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class InvokeGetUserService extends ServiceInvocation {

  private UserService userService;

  public InvokeGetUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long id = Long.parseLong(idStr);
    User user = userService.getUserById(id);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    //Filter out unwanted fields
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(user, FilterFactory.getUserFilter());
    return node;
  }
}
