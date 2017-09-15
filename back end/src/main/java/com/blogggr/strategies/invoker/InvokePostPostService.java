package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.PostsController;
import com.blogggr.entities.Post;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.PostData;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public class InvokePostPostService extends ServiceInvocation {

  private PostService postService;

  public InvokePostPostService(PostService postService) {
    this.postService = postService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, WrongPasswordException {
    ObjectMapper mapper = new ObjectMapper();
    PostData postData;
    try {
      postData = mapper.readValue(body, PostData.class);
    } catch (JsonProcessingException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
    Post post = postService.createPost(userID, postData);
    //Create location string and return it
    Map<String, String> responseMap = new HashMap<>();
    responseMap.put(AppConfig.locationHeaderKey,
        AppConfig.fullBaseUrl + PostsController.postsPath + "/" + String.valueOf(post.getPostId()));
    return responseMap;
  }
}
