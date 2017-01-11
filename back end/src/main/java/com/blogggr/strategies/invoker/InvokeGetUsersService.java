package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.GetUsersValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.01.17.
 */
public class InvokeGetUsersService implements ServiceInvocationStrategy {

    private UserService userService;

    public InvokeGetUsersService(UserService userService){
        this.userService = userService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID){
        String searchString = null;
        if (input.containsKey(GetUsersValidator.searchKey)){
            searchString = input.get(GetUsersValidator.searchKey);
        }
        List<User> users = userService.getUsers(searchString);
        //Filter out unwanted fields
        JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(users, FilterFactory.getUserFilter());
        return node;
    }
}
