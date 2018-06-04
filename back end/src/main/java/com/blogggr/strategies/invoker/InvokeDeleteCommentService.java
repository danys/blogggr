package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.CommentService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokeDeleteCommentService extends ServiceInvocation {

  private CommentService commentService;

  public InvokeDeleteCommentService(CommentService commentService) {
    this.commentService = commentService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DbException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long id = Long.parseLong(idStr);
    commentService.deleteComment(id, userID);
    return null;
  }
}
