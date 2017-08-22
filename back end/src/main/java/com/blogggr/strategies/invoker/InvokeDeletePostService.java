package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.IdValidator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 26.11.16.
 */
public class InvokeDeletePostService extends ServiceInvocation {

  private PostService postService;

  public InvokeDeletePostService(PostService postService) {
    this.postService = postService;
  }

  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, NotAuthorizedException {
    if (!input.containsKey(IdValidator.idName)) {
      return null;
    }
    String idStr = input.get(IdValidator.idName);
    Long id = Long.parseLong(idStr);
    postService.deletePost(id, userID);
    return null;
  }
}
