package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 26.11.16.
 */
public class InvokeDeletePostService implements ServiceInvocationStrategy{

    private PostService postService;

    public InvokeDeletePostService(PostService sessionService){
        this.postService = sessionService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, NotAuthorizedException {
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
        postService.deletePost(id,userID);
        return null;
    }
}
