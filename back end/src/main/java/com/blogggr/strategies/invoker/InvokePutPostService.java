package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.PostData;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokePutPostService implements ServiceInvocationStrategy {

    private PostService postService;

    public InvokePutPostService(PostService postService){
        this.postService = postService;
    }

    @Override
    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, WrongPasswordException, NotAuthorizedException {
        if (!input.containsKey(IdValidator.idName)){
            return null;
        }
        String idStr = input.get(IdValidator.idName);
        Long postID;
        try{
            postID = Long.parseLong(idStr);
        }
        catch(NumberFormatException e){
            return null;
        }
        //Parse the body and perform the update of the associated record
        ObjectMapper mapper = new ObjectMapper();
        PostData postData;
        try{
            postData = mapper.readValue(body, PostData.class);
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
        postService.updatePost(postID, userID, postData);
        return null;
    }
}
