package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.FriendService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class InvokeDeleteFriendService implements ServiceInvocationStrategy {

    private FriendService friendService;

    public InvokeDeleteFriendService(FriendService friendService){
        this.friendService = friendService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException {
        if (!input.containsKey(IdValidator.idName)){
            return null;
        }
        String idStr = input.get(IdValidator.idName);
        Long id = Long.parseLong(idStr);
        friendService.deleteFriend(id,userID);
        return null;
    }
}