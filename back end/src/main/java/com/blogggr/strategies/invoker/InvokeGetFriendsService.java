package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.FriendService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class InvokeGetFriendsService implements ServiceInvocationStrategy {
    private FriendService friendService;

    public InvokeGetFriendsService(FriendService userService){
        this.friendService = friendService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, DBException {
        List<User> friends = friendService.getFriends(userID);
        //Filter out unwanted fields
        Set<String> filter = new HashSet<>();
        filter.add("email");
        filter.add("lastName");
        filter.add("firstName");
        List<JsonNode> nodes = new ArrayList<>();
        for(User user: friends){
            nodes.add(JsonTransformer.filterFieldsOfFlatObject(user,filter));
        }
        return nodes;
    }
}