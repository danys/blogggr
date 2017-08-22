package com.blogggr.strategies.invoker;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.GetPostByLabelValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.05.17.
 */
public class InvokeGetPostByLabelService extends ServiceInvocation {

  private PostService postService;

  public InvokeGetPostByLabelService(PostService postService) {
    this.postService = postService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DBException {
    if (!input.containsKey(GetPostByLabelValidator.userIDKey) || !input
        .containsKey(GetPostByLabelValidator.postShortNameKey)) {
      return null;
    }
    Long posterUserID = Long.parseLong(input.get(GetPostByLabelValidator.userIDKey));
    String postShortTitle = input.get(GetPostByLabelValidator.postShortNameKey);
    Post post = postService.getPostByUserAndLabel(userID, posterUserID, postShortTitle);
    //Filter fields of the post
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(post, FilterFactory.getPostFilter());
    return node;
  }
}
