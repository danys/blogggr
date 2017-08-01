package com.blogggr.strategies.invoker;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.requestdata.UserSearchData;
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
        if (input.containsKey(GetUsersValidator.LIMIT_KEY)){
            limit = Integer.parseInt(input.get(GetUsersValidator.LIMIT_KEY));
        }
        if (input.containsKey(GetUsersValidator.PAGE_KEY)){
            pageNum = Integer.parseInt(input.get(GetUsersValidator.PAGE_KEY));
        }
        if (input.containsKey(GetUsersValidator.SEARCH_KEY)){
            searchString = input.get(GetUsersValidator.SEARCH_KEY);
            RandomAccessListPage<User> usersPage = userService.getUsers(searchString,limit,pageNum);
            //Filter out unwanted fields
            JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(usersPage.getPageItems(), FilterFactory.getUserFilter());
            ObjectMapper mapper = new ObjectMapper();
            List<Object> trimmedUsers = mapper.convertValue(node,List.class);
            return new RandomAccessListPage<>(trimmedUsers,usersPage.getPageData());
        } else {
            //Search by firstName, lastName and email
            UserSearchData searchData = new UserSearchData();
            if (input.containsKey(GetUsersValidator.FIRST_NAME_SEARCH_KEY)) {
                searchData.setFirstName(input.get(GetUsersValidator.FIRST_NAME_SEARCH_KEY));
            }
            if (input.containsKey(GetUsersValidator.LAST_NAME_SEARCH_KEY)) {
                searchData.setLastName(input.get(GetUsersValidator.LAST_NAME_SEARCH_KEY));
            }
            if (input.containsKey(GetUsersValidator.EMAIL_SEARCH_KEY)) {
                searchData.setEmail(input.get(GetUsersValidator.EMAIL_SEARCH_KEY));
            }
            if (input.containsKey(GetUsersValidator.LENGTH_KEY)) {
                searchData.setLength(Integer.parseInt(input.get(GetUsersValidator.LENGTH_KEY)));
            }
            PrevNextListPage<User> users = userService.getUsersBySearchTerms(searchData);
            //Filter out unwanted fields
            JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(users.getPageItems(), FilterFactory.getUserFilter());
            ObjectMapper mapper = new ObjectMapper();
            List<Object> trimmedUsers = mapper.convertValue(node,List.class);
            return new PrevNextListPage<>(trimmedUsers,users.getPageData());
        }
    }
}
