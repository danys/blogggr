package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.PostDao.Visibility;
import com.blogggr.dto.PostSearchData;
import com.blogggr.dto.PrevNextData;
import com.blogggr.dto.SimpleUserSearchData;
import com.blogggr.dto.UserPutData;
import com.blogggr.dto.out.PostDto;
import com.blogggr.dto.out.UserWithImageDto;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.dto.UserSearchData;
import com.blogggr.dto.UserPostData;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.utilities.DtoConverter;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
  private DtoConverter dtoConverter;

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  /**
   * GET /users Search users by email and first name and last name
   *
   * @param userSearchData search users filter
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = USER_PATH, params = "maxRecordsCount")
  public ResponseEntity getUsers(@Valid UserSearchData userSearchData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users] UserSearchData, User: {}", userPrincipal.getUser().getEmail());
    if (userSearchData.getBefore() != null && userSearchData.getAfter() != null) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("UsersController.getUsers.afterBeforeBothSet"));
    }
    PrevNextListPage<User> usersPage = userService.getUsersBySearchTerms(userSearchData);
    List<UserWithImageDto> userWithImageDtos = usersPage.getPageItems().stream()
        .map(user -> dtoConverter.toUserWithImageDto(user))
        .collect(Collectors.toList());
    PrevNextListPage<UserWithImageDto> userWithImageDtoPage = new PrevNextListPage<>(
        userWithImageDtos,
        usersPage.getPageData());
    return ResponseBuilder.getSuccessResponse(userWithImageDtoPage);
  }

  /**
   * GET /users Single search term to search across email, first name and last name
   *
   * @param simpleUserSearchData search term filter, maximum response size and page number
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = USER_PATH, params = "searchString")
  public ResponseEntity getUsersBySearchTerm(@Valid SimpleUserSearchData simpleUserSearchData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users] SimpleUserSearchData, User: {}", userPrincipal.getUser().getEmail());
    RandomAccessListPage<User> usersPage = userService.getUsers(simpleUserSearchData);
    List<UserWithImageDto> userWithImageDtos = usersPage.getPageItems().stream()
        .map(user -> dtoConverter.toUserWithImageDto(user))
        .collect(Collectors.toList());
    RandomAccessListPage<UserWithImageDto> userWithImageDtoPage = new RandomAccessListPage<>(
        userWithImageDtos,
        usersPage.getPageData());
    return ResponseBuilder.getSuccessResponse(userWithImageDtoPage);
  }

  /**
   * GET /users/me Request this users data with its main image
   *
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = USER_PATH + "/me")
  public ResponseEntity getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users/me] User: {}" + userPrincipal.getUser().getEmail());
    User user = userService.getUserByIdWithImages(userPrincipal.getUser().getUserId());
    UserWithImageDto userWithImageDto = dtoConverter.toUserWithImageDto(user);
    return ResponseBuilder.getSuccessResponse(userWithImageDto);
  }

  /**
   * GET /users/id
   *
   * @param id the id of the user to be fetched
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = USER_PATH + "/{id:[\\d]+}")
  public ResponseEntity getUser(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /users/id] Id: {}, user: {}", id, userPrincipal.getUser().getEmail());
    User user = userService.getUserByIdWithImages(Long.parseLong(id));
    UserWithImageDto userWithImageDto = dtoConverter.toUserWithImageDto(user);
    return ResponseBuilder.getSuccessResponse(userWithImageDto);
  }

  /**
   * GET /users/id/posts Retrieve the posts of a particular user
   *
   * @param id the id of the user whose posts will be fetched
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = USER_PATH + "/{id:[\\d]+}/posts")
  public ResponseEntity getUserPosts(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal, @Valid PrevNextData<Long> searchData) {
    logger.info("[GET /users/id/posts] Id: {}, user: {}", id, userPrincipal.getUser().getEmail());
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setPosterUserId(Long.parseLong(id));
    postSearchData.setVisibility(Visibility.all);
    postSearchData.setMaxRecordsCount(searchData.getMaxRecordsCount());
    postSearchData.setBefore(searchData.getBefore());
    postSearchData.setAfter(searchData.getAfter());
    PrevNextListPage<Post> page = postService
        .getPosts(postSearchData, userPrincipal.getUser());
    List<PostDto> postDtos = page.getPageItems().stream()
        .map(post -> dtoConverter.toPostDto(post))
        .collect(Collectors.toList());
    PrevNextListPage<PostDto> postDtoPage = new PrevNextListPage<>(postDtos,
        page.getPageData());
    return ResponseBuilder.getSuccessResponse(postDtoPage);
  }

  /**
   * POST /users Create a new user
   *
   * @param userData the data of the new user
   */
  @PostMapping(value = USER_PATH)
  public ResponseEntity createUser(@Valid @RequestBody UserPostData userData) {
    logger.info("[POST /users] Create user with email: {}", userData.getEmail());
    User user = userService.createUser(userData);
    return ResponseBuilder.postSuccessResponse(
        AppConfig.fullBaseUrl + USER_PATH + '/' + String.valueOf(user.getUserId()));
  }

  /**
   * PUT /users/id
   *
   * @param id the userId of the user to update
   * @param userData the new data of the user
   * @param userPrincipal the logged in user
   */
  @PutMapping(value = USER_PATH + "/{id:[\\d]+}")
  public ResponseEntity updateUser(@PathVariable String id,
      @Valid @RequestBody UserPutData userData,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, NotAuthorizedException {
    logger.info("[PUT /users/id] Id: {}. User: {}", id, userPrincipal.getUser().getEmail());
    userService.updateUser(Long.parseLong(id), userPrincipal.getUser().getUserId(), userData);
    return ResponseBuilder.putSuccessResponse();
  }
}
