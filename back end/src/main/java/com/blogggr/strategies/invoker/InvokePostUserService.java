package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.UsersController;
import com.blogggr.entities.User;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
public class InvokePostUserService implements ServiceInvocationStrategy{

    private UserService userService;

    public InvokePostUserService(UserService userService){
        this.userService = userService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID){
        ObjectMapper mapper = new ObjectMapper();
        UserPostData userData;
        try{
            userData = mapper.readValue(body, UserPostData.class);
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
        User user = userService.createUser(userData);
        //Create location string and return it
        return AppConfig.fullBaseUrl + UsersController.userPath + "/" + String.valueOf(user.getUserID());
    }

}
