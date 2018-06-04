package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.UserPutData;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokePutUserService extends ServiceInvocation {

  private UserService userService;

  public InvokePutUserService(UserService commentService) {
    this.userService = commentService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DbException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long userResourceID = Long.parseLong(idStr);
    //Parse the body and perform the update of the associated record
    ObjectMapper mapper = new ObjectMapper();
    UserPutData userData;
    try {
      userData = mapper.readValue(body, UserPutData.class);
    } catch (JsonProcessingException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
    userService.updateUser(userResourceID, userID, userData);
    return null;
  }
}
