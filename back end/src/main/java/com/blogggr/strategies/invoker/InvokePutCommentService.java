package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;
import com.blogggr.services.CommentService;
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
public class InvokePutCommentService implements ServiceInvocationStrategy {

  private CommentService commentService;

  public InvokePutCommentService(CommentService commentService) {
    this.commentService = commentService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long commentID = Long.parseLong(idStr);
    //Parse the body and perform the update of the associated record
    ObjectMapper mapper = new ObjectMapper();
    CommentData commentData;
    try {
      commentData = mapper.readValue(body, CommentData.class);
    } catch (JsonProcessingException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
    commentService.updateComment(commentID, userID, commentData);
    return null;
  }
}