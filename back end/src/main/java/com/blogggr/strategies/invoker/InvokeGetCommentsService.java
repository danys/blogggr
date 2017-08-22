package com.blogggr.strategies.invoker;

import com.blogggr.entities.Comment;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.CommentService;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class InvokeGetCommentsService extends ServiceInvocation {

  private PostService postService;
  private CommentService commentService;

  public InvokeGetCommentsService(PostService postService, CommentService commentService) {
    this.postService = postService;
    this.commentService = commentService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DBException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long id = Long.parseLong(idStr);
    List<Comment> comments = commentService.getCommentsByPostId(id, userID);
    if (comments == null) {
      throw new ResourceNotFoundException("Comments not found!");
    }
    //Filter comment fields
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(comments, FilterFactory.getCommentFilter());
    return node;
  }
}
