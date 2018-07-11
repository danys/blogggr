package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.CommentData;
import com.blogggr.dto.out.CommentDto;
import com.blogggr.entities.Comment;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.CommentService;
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

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@RestController
@RequestMapping(AppConfig.BASE_URL)
public class CommentsController {

  public static final String commentsPath = "/comments";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CommentService commentService;

  @Autowired
  private DtoConverter dtoConverter;

  /**
   * POST /comments
   *
   * @param commentData the comment content
   * @param userPrincipal the logged in user
   */
  @PostMapping(value = commentsPath)
  public ResponseEntity createComment(@Valid @RequestBody CommentData commentData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[POST /comments] User: {}", userPrincipal.getUser().getEmail());
    Comment comment = commentService
        .createComment(userPrincipal.getUser().getUserId(), commentData);
    return ResponseBuilder
        .postSuccessResponse(AppConfig.FULL_BASE_URL + commentsPath + '/' + comment.getCommentId());
  }

  /**
   * PUT /comments/id
   *
   * @param id the id of the comment
   * @param commentData the updated comment content
   * @param userPrincipal the logged in user
   */
  @PutMapping(value = commentsPath + "/{id:[\\d]+}")
  public ResponseEntity updateComment(@PathVariable String id, @Valid @RequestBody CommentData commentData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info(
        "[PUT /comments/id] Id: {}. User: {}", id, userPrincipal.getUser().getEmail());
    commentService
        .updateComment(Long.parseLong(id), userPrincipal.getUser().getUserId(), commentData);
    return ResponseBuilder.putSuccessResponse();
  }

  /**
   * DELETE /comments/id
   *
   * @param id the id of the comment to delete
   * @param userPrincipal the logged in user
   */
  @DeleteMapping(value = commentsPath + "/{id:[\\d]+}")
  public ResponseEntity deleteComment(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[DELETE /comments] Id: {}. User: {}", id, userPrincipal.getUser().getEmail());
    commentService.deleteComment(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.deleteSuccessResponse();
  }

  /**
   * GET /comments/id
   *
   * @param id the id of the comment to fetch
   * @param userPrincipal the logged in user
   */
  @GetMapping(value = commentsPath + "/{id:[\\d]+}")
  public ResponseEntity getComment(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /comments/id] Id: {}, User: {}", id, userPrincipal.getUser().getEmail());
    Comment comment = commentService
        .getCommentById(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.getSuccessResponse(dtoConverter.toCommentDto(comment));
  }

  /**
   * GET /posts/id/comments
   * @param id the id of the post for which comments should be fetched
   * @param userPrincipal the logged in user
   * @return
   */
  @GetMapping(value = PostsController.postsPath + "/{id:[\\d]+}"
      + commentsPath)
  public ResponseEntity getCommentsByPostId(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /comments/id/comments] Id: {}. User: {}", id,
        userPrincipal.getUser().getEmail());
    List<Comment> comments = commentService
        .getCommentsByPostId(Long.parseLong(id), userPrincipal.getUser().getUserId());
    List<CommentDto> commentDtos = comments.stream().map(comment -> dtoConverter.toCommentDto(comment))
        .collect(Collectors.toList());
    return ResponseBuilder.getSuccessResponse(commentDtos);
  }

}
