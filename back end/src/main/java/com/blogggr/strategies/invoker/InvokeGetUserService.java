package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.GetIdValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class InvokeGetUserService implements ServiceInvocationStrategy {
    private UserService userService;

    public InvokeGetUserService(UserService userService){
        this.userService = userService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID){
        if (!input.containsKey(GetIdValidator.idName)){
            return null;
        }
        String idStr = input.get(GetIdValidator.idName);
        Long id;
        try{
            id = Long.parseLong(idStr);
        }
        catch(NumberFormatException e){
            return null;
        }
        User user = userService.getUserById(id);
        //Filter out unwanted fields
        Set<String> filter = new HashSet<>();
        filter.add("email");
        filter.add("lastName");
        filter.add("firstName");
        JsonNode node = JsonTransformer.filterFieldsOfFlatObject(user,filter);
        return node;
    }
}
