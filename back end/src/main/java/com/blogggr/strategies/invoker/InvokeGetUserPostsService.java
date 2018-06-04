package com.blogggr.strategies.invoker;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.GetPostsValidator;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.01.17.
 */
public class InvokeGetUserPostsService extends ServiceInvocation {

  private PostService postService;

  public InvokeGetUserPostsService(PostService postService) {
    this.postService = postService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, DbException {
    //Check if the poster id is present
    Long posterID = null;
    if (input.containsKey(IdValidator.idName)) {
      posterID = Long.parseLong(input.get(IdValidator.idName));
    }
    //Before key
    Long before = null;
    if (input.containsKey(GetPostsValidator.beforeKey)) {
      before = Long.parseLong(input.get(GetPostsValidator.beforeKey));
    }
    //After key
    Long after = null;
    if (input.containsKey(GetPostsValidator.afterKey)) {
      after = Long.parseLong(input.get(GetPostsValidator.afterKey));
    }
    //Limit key
    Integer limit = null;
    if (input.containsKey(GetPostsValidator.limitKey)) {
      limit = Integer.parseInt(input.get(GetPostsValidator.limitKey));
    }
    PrevNextListPage<Post> page = postService
        .getPosts(userID, posterID, null, null, before, after, limit);
    List<Post> posts = page.getPageItems();
    //Filter attributes of the posts
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(posts, FilterFactory.getReducedPostFilter());
    ObjectMapper mapper = new ObjectMapper();
    List<Object> trimmedPosts = mapper.convertValue(node, List.class);
    PrevNextListPage<Object> resultPage = new PrevNextListPage<>(trimmedPosts, page.getPageData());
    return resultPage;
  }

}
