package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.PostDao;
import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonTransformer;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocation;
import com.blogggr.strategies.validators.GetPostsValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.12.16.
 */
public class InvokeGetPostsService extends ServiceInvocation {

  private PostService postService;

  public InvokeGetPostsService(PostService postService) {
    this.postService = postService;
  }

  @Override
  public Object invokeService(Map<String, String> input, String body, Long userID)
      throws ResourceNotFoundException, DBException {
    //Check if the poster id is present
    String idStr = null;
    Long posterID = null;
    if (input.containsKey(GetPostsValidator.posterUserIDKey)) {
      long idp = Long.parseLong(input.get(GetPostsValidator.posterUserIDKey));
      posterID = new Long(idp);
    }
    //Check if titleKey is present
    String title = null;
    if (input.containsKey(GetPostsValidator.titleKey)) {
      title = input.get(GetPostsValidator.titleKey);
    }
    //Check the visibility setting
    String visibility = null;
    PostDao.Visibility visi = PostDao.Visibility.all; //return all (from friends, global and current user) posts by default
    if (input.containsKey(GetPostsValidator.visibilityKey)) {
      visibility = input.get(GetPostsValidator.visibilityKey);
    }
    if (visibility != null) {
      if (visibility.compareTo(PostDao.Visibility.onlyGlobal.name()) == 0) {
        visi = PostDao.Visibility.onlyGlobal;
      } else if (visibility.compareTo(PostDao.Visibility.all.name()) == 0) {
        visi = PostDao.Visibility.all;
      } else if (visibility.compareTo(PostDao.Visibility.onlyFriends.name()) == 0) {
        visi = PostDao.Visibility.onlyFriends;
      } else if (visibility.compareTo(PostDao.Visibility.onlyCurrentUser.name()) == 0) {
        visi = PostDao.Visibility.onlyCurrentUser;
      }
    } else if (visibility == null) {
      visi = PostDao.Visibility.all;
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
        .getPosts(userID, posterID, title, visi, before, after, limit);
    List<Post> posts = page.getPageItems();
    //Shorten and append ... if the post's text is too long. Load image.
    posts.forEach(post -> {
      post.getUser().getImage();
      if (post.getTextBody() != null
          && post.getTextBody().length() > AppConfig.maxPostBodyLength) {
        post.setTextBody(post.getTextBody().substring(0, AppConfig.maxPostBodyLength) + "...");
      }
    });
    //Filter attributes of the posts
    JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(posts, FilterFactory.getReducedPostFilter());
    ObjectMapper mapper = new ObjectMapper();
    List<Object> trimmedPosts = mapper.convertValue(node, List.class);
    PrevNextListPage<Object> resultPage = new PrevNextListPage<>(trimmedPosts, page.getPageData());
    return resultPage;
  }
}
