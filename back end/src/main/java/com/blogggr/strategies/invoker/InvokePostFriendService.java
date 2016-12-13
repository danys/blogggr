package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.FriendsController;
import com.blogggr.entities.Friend;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.FriendData;
import com.blogggr.services.FriendService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.12.16.
 */
public class InvokePostFriendService implements ServiceInvocationStrategy {

    private FriendService friendService;

    public InvokePostFriendService(FriendService friendService){
        this.friendService = friendService;
    }

    @Override
    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, DBException, NotAuthorizedException{
        ObjectMapper mapper = new ObjectMapper();
        FriendData friendData;
        try{
            friendData = mapper.readValue(body, FriendData.class);
        }
        catch(JsonParseException e){
            return null;
        }
        catch(JsonProcessingException e){
            return null;
        }
        catch(IOException e){
            return null;
        }
        Friend friend = friendService.createFriend(userID, friendData);
        //Create location string and return it
        //Create location string and session id hash. Then return it as a map.
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AppConfig.locationHeaderKey,AppConfig.fullBaseUrl + FriendsController.friendsPath + "/" + String.valueOf(friend.getId()));
        return responseMap;
    }
}
