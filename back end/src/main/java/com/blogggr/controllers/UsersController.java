package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.UserDto;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DbException;
import com.blogggr.models.*;
import com.blogggr.dto.UserSearchData;
import com.blogggr.dto.UserPostData;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.*;
import com.blogggr.strategies.responses.GetResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.*;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.DtoConverter;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserService userService;

  @Autowired
  private PostService postService;

  @Autowired
  private Cryptography cryptography;

  @Autowired
  private DtoConverter dtoConverter;

  //GET /users
  /*@RequestMapping(path = USER_PATH, method = RequestMethod.GET)
  public ResponseEntity getUsers(@RequestParam Map<String, String> params,
      @RequestHeader Map<String, String> header) {
    logger.info(
        "[GET /users] RequestParams: " + params.toString() + ". Header: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new GetUsersValidator(), new InvokeGetUsersService(userService), new GetResponse());
    return model.execute(params, header, null);
  }*/

  //GET /users
  @RequestMapping(path = USER_PATH, method = RequestMethod.GET)
  public ResponseEntity getUsers(@Valid UserSearchData userSearchData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws DbException {
    PrevNextListPage<User> usersPage = userService.getUsersBySearchTerms(userSearchData);
    List<UserDto> userDtos = usersPage.getPageItems().stream().map(user -> dtoConverter.toUserDto(user))
        .collect(Collectors.toList());
    PrevNextListPage<UserDto> userDtoPage = new PrevNextListPage<>(userDtos, usersPage.getPageData());
    return ResponseBuilder.successResponse(userDtoPage);
    //Filter out unwanted fields
    /*JsonNode node = JsonTransformer
        .filterFieldsOfMultiLevelObject(users.getPageItems(), FilterFactory.getUserFilter());
    ObjectMapper mapper = new ObjectMapper();
    List<Object> trimmedUsers = mapper.convertValue(node, List.class);
    return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(new PrevNextListPage<>(trimmedUsers, users.getPageData())), HttpStatus.OK);*/
  }

  //GET /users/me
  @RequestMapping(path = USER_PATH + "/me", method = RequestMethod.GET)
  public ResponseEntity getCurrentUser(@RequestHeader Map<String, String> header,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users/me] RequestHeader: " + header.toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new NoCheckValidator(), new InvokeGetUserMeService(userService), new GetResponse());
    return model.execute(null, header, null);
  }

  //GET /users/id
  @RequestMapping(path = USER_PATH + "/{id:[\\d]+}", method = RequestMethod.GET)
  public ResponseEntity getUser(@PathVariable String id,
      @RequestHeader Map<String, String> header,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users/id] Id: " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeGetUserService(userService), new GetResponse());
    return model.execute(map, header, null);
  }

  //GET /users/id/posts
  @RequestMapping(path = USER_PATH + "/{id}/posts", method = RequestMethod.GET)
  public ResponseEntity getUserPosts(@PathVariable String id,
      @RequestHeader Map<String, String> header,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users/id/posts] Id: " + id + ". Header: " + header.toString());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new IdValidator(), new InvokeGetUserPostsService(postService), new GetResponse());
    return model.execute(map, header, null);
  }

  //POST /users
  @RequestMapping(path = USER_PATH, method = RequestMethod.POST)
  public User createUser(@RequestBody @Valid UserPostData userPostData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[POST /users] RequestBody: " + userPostData);
    return userService.createUser(userPostData);
    /*AppModel model = new AppModelImpl(new NoAuthorization(), new UserPostDataValidator(),
        new InvokePostUserService(userService), new PostResponse());
    return model.execute(null, null, bodyData);*/
  }

  //PUT /users/id
  @RequestMapping(path = USER_PATH + "/{id}", method = RequestMethod.PUT)
  public ResponseEntity updateUser(@PathVariable String id, @RequestBody String bodyData,
      @RequestHeader Map<String, String> header,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[PUT /users/id] Id: " + id + ". RequestBody: " + bodyData + ". Header: " + header
        .toString());
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        new UserPutDataValidator(), new InvokePutUserService(userService), new PutResponse());
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    return model.execute(map, header, bodyData);
  }
}
