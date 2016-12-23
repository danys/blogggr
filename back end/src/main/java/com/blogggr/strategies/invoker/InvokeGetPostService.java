package com.blogggr.strategies.invoker;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
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
        Map<String,Set<String>> filterMap = new HashMap<>();
        filterMap.put("postID",null);
        filterMap.put("shortTitle",null);
        filterMap.put("textBody",null);
        filterMap.put("timestamp",null);
        filterMap.put("title",null);
        filterMap.put("global",null);
        Set<String> userFilter = new HashSet<>();
        userFilter.add("userID");
        userFilter.add("email");
        userFilter.add("lastName");
        userFilter.add("firstName");
        filterMap.put("user",userFilter);
        Set<String> commentFilter = new HashSet<>();
        commentFilter.add("commentID");
        commentFilter.add("text");
        commentFilter.add("timestamp");
        filterMap.put("comments",commentFilter);
        JsonNode node = JsonTransformer.filterFieldsOfTwoLevelObject(post,filterMap);
        return node;
    }
}
