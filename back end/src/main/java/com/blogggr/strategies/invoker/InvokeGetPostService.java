package com.blogggr.strategies.invoker;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

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
        //No fields of the post are filtered => return post directly
        return post;
    }
}
