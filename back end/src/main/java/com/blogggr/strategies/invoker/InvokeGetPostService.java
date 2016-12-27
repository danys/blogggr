package com.blogggr.strategies.invoker;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.JsonFilter;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel Sunnen on 04.12.16.
 */
public class InvokeGetPostService implements ServiceInvocationStrategy {

    private PostService postService;

    public InvokeGetPostService(PostService postService){
        this.postService = postService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException {
        if (!input.containsKey(IdValidator.idName)){
            return null;
        }
        String idStr = input.get(IdValidator.idName);
        Long id;
        try{
            id = Long.parseLong(idStr);
        }
        catch(NumberFormatException e){
            return null;
        }
        Post post = postService.getPostById(id, userID);
        if (post==null) throw new ResourceNotFoundException("Post not found!");
        //Filter fields of the post => return post directly

        Map<String, JsonFilter> filterMap = new HashMap<>();
        filterMap.put("postID",null);
        filterMap.put("shortTitle",null);
        filterMap.put("textBody",null);
        filterMap.put("timestamp",null);
        filterMap.put("title",null);
        filterMap.put("global",null);
        Map<String, JsonFilter> userFilterMap = new HashMap<>();
        userFilterMap.put("userID",null);
        userFilterMap.put("email",null);
        userFilterMap.put("lastName",null);
        userFilterMap.put("firstName",null);
        JsonFilter userFilter = new JsonFilter(userFilterMap);
        filterMap.put("user",userFilter);
        Map<String, JsonFilter> commentFilterMap = new HashMap<>();
        commentFilterMap.put("commentID",null);
        commentFilterMap.put("text",null);
        commentFilterMap.put("timestamp",null);
        commentFilterMap.put("user",userFilter);
        JsonFilter commentFilter = new JsonFilter(commentFilterMap);
        filterMap.put("commentsArray",commentFilter);
        JsonFilter jsonFilter = new JsonFilter(filterMap);
        JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(post,jsonFilter);
        return node;
    }
}
