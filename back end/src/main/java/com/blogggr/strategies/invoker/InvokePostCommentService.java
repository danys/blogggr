package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.CommentsController;
import com.blogggr.entities.Comment;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;
import com.blogggr.services.CommentService;
import com.blogggr.strategies.ServiceInvocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public class InvokePostCommentService extends ServiceInvocation {

  private CommentService commentService;

  public InvokePostCommentService(CommentService commentService) {
    this.commentService = commentService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, DBException, NotAuthorizedException {
    ObjectMapper mapper = new ObjectMapper();
    CommentData commentData;
    try {
      commentData = mapper.readValue(body, CommentData.class);
    } catch (JsonProcessingException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
    Comment comment = commentService.createComment(userID, commentData);
    //Create location string and return it
    //Create location string and session id hash. Then return it as a map.
    Map<String, String> responseMap = new HashMap<>();
    String put = responseMap.put(AppConfig.locationHeaderKey,
        AppConfig.fullBaseUrl + CommentsController.commentsPath + "/" + String
            .valueOf(comment.getCommentId()));
    return responseMap;
  }
}
