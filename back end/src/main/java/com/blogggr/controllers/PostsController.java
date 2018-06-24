package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.PostData;
import com.blogggr.dto.PostDataUpdate;
import com.blogggr.dto.PostSearchData;
import com.blogggr.dto.out.PostDto;
import com.blogggr.entities.Post;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.PostService;
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
 * Created by Daniel Sunnen on 19.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class PostsController {

  public static final String postsPath = "/posts";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PostService postService;

  @Autowired
  private DtoConverter dtoConverter;

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  /**
   * POST /posts
   *
   * @param postData the post's data
   * @param userPrincipal the logged in user
   */
  @PostMapping(value = postsPath)
  public ResponseEntity createPost(@Valid @RequestBody PostData postData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws ResourceNotFoundException {
    logger.info("[POST /posts] Post title: {}. User: {}", postData.getTitle(),
        userPrincipal.getUser().getEmail());
    Post post = postService.createPost(userPrincipal.getUser().getUserId(), postData);
    return ResponseBuilder
        .postSuccessResponse(AppConfig.fullBaseUrl + postsPath + '/' + post.getPostId());
  }

  /**
   * PUT /posts
   *
   * @param id the id of the post to update
   * @param postData the updated post's data
   * @param userPrincipal the logged in user
   */
  @PutMapping(value = postsPath + "/{id:[\\d]+}")
  public ResponseEntity updatePost(@PathVariable String id, @RequestBody PostDataUpdate postData,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws NotAuthorizedException, ResourceNotFoundException {
    logger.info("[PUT /posts] Id: {}, User: {}", id, userPrincipal.getUser().getEmail());
    if (postData.getTitle() == null && postData.getTextBody() == null
        && postData.getIsGlobal() == null) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("PostController.updatePost.allFieldsNil"));
    }
    postService.updatePost(Long.parseLong(id), userPrincipal.getUser().getUserId(), postData);
    return ResponseBuilder.putSuccessResponse();
  }

  /**
   * DELETE /posts/id
   *
   * @param id the id of the post to delete
   * @param userPrincipal the logged in user
   */
  @DeleteMapping(value = postsPath + "/{id:[\\d]+}")
  public ResponseEntity deletePost(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, NotAuthorizedException {
    logger.info("[DELETE /posts] Id: {}. User: {}", id, userPrincipal.getUser().getEmail());
    postService.deletePost(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.deleteSuccessResponse();
  }

  /**
   * GET /posts/id
   *
   * @param id the id of the post to return
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = postsPath + "/{id:[\\d]+}")
  public ResponseEntity getPost(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, NotAuthorizedException {
    logger.info("[GET /posts/id] Id: {}. User: {}", id, userPrincipal.getUser().getEmail());
    Post post = postService.getPostById(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.getSuccessResponse(dtoConverter.toPostDto(post));
  }

  /**
   * GET /users/{userID}/posts/{post-short-name}
   *
   * @param userId the user whose posts should
   * @param postShortName the short name of a post
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = UsersController.USER_PATH
      + "/{userID:[\\d]+]}/posts/{postShortName:[\\.]+}")
  public ResponseEntity getPost(@PathVariable String userId, @PathVariable String postShortName,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws NotAuthorizedException, ResourceNotFoundException {
    logger.info(
        "[GET /users/{}/posts/{}] User:{}", userId, postShortName,
        userPrincipal.getUser().getEmail());
    if (postShortName.length() < 3) {
      throw new IllegalArgumentException(simpleBundleMessageSource.getMessage(""));
    }
    Post post = postService
        .getPostByUserAndLabel(userPrincipal.getUser().getUserId(), Long.parseLong(userId),
            postShortName);
    return ResponseBuilder.getSuccessResponse(dtoConverter.toPostDto(post));
  }

  /**
   * GET /posts
   *
   * @param postSearchData the query data
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = postsPath)
  public ResponseEntity getPosts(@Valid PostSearchData postSearchData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info(
        "[GET /posts] User: {}", userPrincipal.getUser().getEmail());
    PrevNextListPage<Post> page = postService
        .getPosts(postSearchData, userPrincipal.getUser());
    List<PostDto> postDtos = page.getPageItems().stream().map(post -> dtoConverter.toPostDto(post))
        .collect(
            Collectors.toList());
    PrevNextListPage<PostDto> dtoPage = new PrevNextListPage<>(postDtos, page.getPageData());
    return ResponseBuilder.getSuccessResponse(dtoPage);
  }
}
