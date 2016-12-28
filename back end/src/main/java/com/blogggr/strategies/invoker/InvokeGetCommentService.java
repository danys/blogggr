package com.blogggr.strategies.invoker;

import com.blogggr.entities.Comment;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.JsonFilter;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.CommentService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokeGetCommentService implements ServiceInvocationStrategy {

    private CommentService commentService;

    public InvokeGetCommentService(CommentService commentService){
        this.commentService = commentService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, NotAuthorizedException{
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
        Comment comment = commentService.getCommentById(id, userID);
        if (comment==null) throw new ResourceNotFoundException("Comment not found!");
        //Filter comment fields
        Map<String, JsonFilter> filterMap = new HashMap<>();
        filterMap.put("commentID",null);
        filterMap.put("text",null);
        filterMap.put("timestamp",null);
        Map<String, JsonFilter> userFilterMap = new HashMap<>();
        userFilterMap.put("userID",null);
        userFilterMap.put("email",null);
        userFilterMap.put("lastName",null);
        userFilterMap.put("firstName",null);
        JsonFilter userFilter = new JsonFilter(userFilterMap);
        filterMap.put("user",userFilter);
        JsonFilter jsonFilter = new JsonFilter(filterMap);
        JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(comment,jsonFilter);
        return node;
    }
}
