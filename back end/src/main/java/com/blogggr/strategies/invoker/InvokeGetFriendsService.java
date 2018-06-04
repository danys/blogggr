package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.FriendService;
import com.blogggr.strategies.ServiceInvocation;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class InvokeGetFriendsService extends ServiceInvocation {

  private FriendService friendService;

  public InvokeGetFriendsService(FriendService friendService) {
    this.friendService = friendService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, DbException {
    List<User> friends = friendService.getFriends(userID);
    //Filter out unwanted fields
    JsonNode nodes = JsonTransformer
        .filterFieldsOfMultiLevelObject(friends, FilterFactory.getUserFilter());
    return nodes;
  }
}