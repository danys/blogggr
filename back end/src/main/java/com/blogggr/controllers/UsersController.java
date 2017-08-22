package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.*;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.auth.NoAuthorization;
import com.blogggr.strategies.invoker.*;
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
 * Created by Daniel Sunnen on 24.10.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class UsersController {

  public static final String USER_PATH = "/users";

  private UserService userService;
  private PostService postService;
  private Cryptography cryptography;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public UsersController(UserService userService, PostService postService,
      Cryptography cryptography) {
    this.userService = userService;
    this.postService = postService;
    this.cryptography = cryptography;
  }

  //GET /users
  @RequestMapping(path = USER_PATH, method = RequestMethod.GET)
  public ResponseEntity getUsers(@RequestParam Map<String, String> params,
      @RequestHeader Map<String, String> header) {
    logger.debug(
        "[GET /users] RequestParams: " + params.toString() + ". Header: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new GetUsersValidator(), new InvokeGetUsersService(userService), new GetResponse());
    return model.execute(params, header, null);
  }

  //GET /users/me
  @RequestMapping(path = USER_PATH + "/me", method = RequestMethod.GET)
  public ResponseEntity getCurrentUser(@RequestHeader Map<String, String> header) {
    logger.debug("[GET /users/me] RequestHeader: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new NoCheckValidator(), new InvokeGetUserMeService(userService), new GetResponse());
    return model.execute(null, header, null);
  }

  //GET /users/id
  @RequestMapping(path = USER_PATH + "/{id:[\\d]+}", method = RequestMethod.GET)
  public ResponseEntity getUser(@PathVariable String id,
      @RequestHeader Map<String, String> header) {
    logger.debug("[GET /users/id] Id: " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeGetUserService(userService), new GetResponse());
    return model.execute(map, header, null);
  }

  //GET /users/id/posts
  @RequestMapping(path = USER_PATH + "/{id}/posts", method = RequestMethod.GET)
  public ResponseEntity getUserPosts(@PathVariable String id,
      @RequestHeader Map<String, String> header) {
    logger.debug("[GET /users/id/posts] Id: " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeGetUserPostsService(postService), new GetResponse());
    return model.execute(map, header, null);
  }

  //POST /users
  @RequestMapping(path = USER_PATH, method = RequestMethod.POST)
  public ResponseEntity createUser(@RequestBody String bodyData) {
    logger.debug("[POST /users] RequestBody: " + bodyData);
    AppModel model = new AppModelImpl(new NoAuthorization(), new UserPostDataValidator(),
        new InvokePostUserService(userService), new PostResponse());
    return model.execute(null, null, bodyData);
  }

  //PUT /users/id
  @RequestMapping(path = USER_PATH + "/{id}", method = RequestMethod.PUT)
  public ResponseEntity updateUser(@PathVariable String id, @RequestBody String bodyData,
      @RequestHeader Map<String, String> header) {
    logger.debug("[PUT /users/id] Id: " + id + ". RequestBody: " + bodyData + ". Header: " + header
        .toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new UserPutDataValidator(), new InvokePutUserService(userService), new PutResponse());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    return model.execute(map, header, bodyData);
  }
}
