package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.CommentService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokeDeleteCommentService implements ServiceInvocationStrategy {

    private CommentService commentService;

    public InvokeDeleteCommentService(CommentService commentService){
        this.commentService = commentService;
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
        commentService.deleteComment(id,userID);
        return null;
    }
}
