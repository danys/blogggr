package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.services.UserService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.GetUsersValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public Object invokeService(Map<String,String> input, String body, Long userID) throws DBException{
        String searchString = null;
        Integer limit = null;
        Integer pageNum = null;
        if (input.containsKey(GetUsersValidator.searchKey)){
            searchString = input.get(GetUsersValidator.searchKey);
        }
        if (input.containsKey(GetUsersValidator.limitKey)){
            limit = Integer.parseInt(input.get(GetUsersValidator.limitKey));
        }
        if (input.containsKey(GetUsersValidator.pageKey)){
            pageNum = Integer.parseInt(input.get(GetUsersValidator.pageKey));
        }
        RandomAccessListPage<User> usersPage = userService.getUsers(searchString,limit,pageNum);
        //Filter out unwanted fields
        JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(usersPage.getPageItems(), FilterFactory.getUserFilter());
        ObjectMapper mapper = new ObjectMapper();
        List<Object> trimmedUsers = mapper.convertValue(node,List.class);
        RandomAccessListPage<Object> resultPage = new RandomAccessListPage<>(trimmedUsers,usersPage.getPageData());
        return resultPage;
    }
}
