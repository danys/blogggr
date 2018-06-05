package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.dto.FriendData;
import com.blogggr.services.FriendService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.FriendPutDataValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class InvokePutFriendService extends ServiceInvocation {

  private FriendService friendService;

  public InvokePutFriendService(FriendService friendService) {
    this.friendService = friendService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DbException {
    if (!input.containsKey(FriendPutDataValidator.idName) && !input
        .containsKey(FriendPutDataValidator.id2Name)) {
      return null;
    }
    String idStr = input.get(FriendPutDataValidator.idName);
    String id2Str = input.get(FriendPutDataValidator.id2Name);
    long id1, id2;
    id1 = Long.parseLong(idStr);
    id2 = Long.parseLong(id2Str);
    //Parse the body and perform the update of the associated record
    ObjectMapper mapper = new ObjectMapper();
    FriendData friendData;
    try {
      friendData = mapper.readValue(body, FriendData.class);
    } catch (JsonProcessingException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
    friendService.updateFriend(userID, id1, id2, friendData);
    return null;
  }
}