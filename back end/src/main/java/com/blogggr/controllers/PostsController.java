package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.*;
import com.blogggr.strategies.responses.DeleteResponse;
import com.blogggr.strategies.responses.GetResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.*;
import com.blogggr.utilities.Cryptography;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class PostsController {

  public static final String postsPath = "/posts";

  private UserService userService;
  private PostService postService;
  private Cryptography cryptography;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public PostsController(UserService userService, PostService postService,
      Cryptography cryptography) {
    this.userService = userService;
    this.postService = postService;
    this.cryptography = cryptography;
  }

  //POST /posts
  @RequestMapping(path = postsPath, method = RequestMethod.POST)
  public ResponseEntity createPost(@RequestBody String bodyData,
      @RequestHeader Map<String, String> header) {
    logger.debug("[POST /posts] RequestBody: " + bodyData + ". Header: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new PostPostDataValidator(), new InvokePostPostService(postService), new PostResponse());
    return model.execute(null, header, bodyData);
  }

  //PUT /posts
  @RequestMapping(path = postsPath + "/{id}", method = RequestMethod.PUT)
  public ResponseEntity updatePost(@PathVariable String id, @RequestBody String bodyData,
      @RequestHeader Map<String, String> header) {
    logger.debug("[PUT /posts] Id = " + id + ". RequestBody: " + bodyData + ". Header: " + header
        .toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new PostPutDataValidator(), new InvokePutPostService(postService), new PutResponse());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    return model.execute(map, header, bodyData);
  }

  //DELETE /posts
  @RequestMapping(path = postsPath + "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deletePost(@PathVariable String id,
      @RequestHeader Map<String, String> header) {
    logger.debug("[DELETE /posts] Id = " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeDeletePostService(postService), new DeleteResponse());
    return model.execute(map, header, null);
  }

  //GET /posts/id
  @RequestMapping(path = postsPath + "/{id}", method = RequestMethod.GET)
  public ResponseEntity getPost(@PathVariable String id,
      @RequestHeader Map<String, String> header) {
    logger.debug("[GET /posts/id] Id = " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeGetPostService(postService), new GetResponse());
    return model.execute(map, header, null);
  }

  //GET /users/{userID}/posts/{post-short-name}
  @RequestMapping(path = UsersController.USER_PATH
      + "/{userID}/posts/{postShortName}", method = RequestMethod.GET)
  public ResponseEntity getPost(@PathVariable String userID, @PathVariable String postShortName,
      @RequestHeader Map<String, String> header) {
    logger.debug(
        "[GET /users/{userID}/posts/{post-short-name}] UserID = " + userID + ". PostShortName = "
            + postShortName + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("userID", userID);
    map.put("postShortName", postShortName);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new GetPostByLabelValidator(), new InvokeGetPostByLabelService(postService),
        new GetResponse());
    return model.execute(map, header, null);
  }

  //GET /posts
  @RequestMapping(path = postsPath, method = RequestMethod.GET)
  public ResponseEntity getPosts(@RequestParam Map<String, String> params,
      @RequestHeader Map<String, String> header) {
    logger.debug(
        "[GET /posts] RequestParams = " + params.toString() + ". Header: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new GetPostsValidator(), new InvokeGetPostsService(postService), new GetResponse());
    return model.execute(params, header, null);
  }
}
