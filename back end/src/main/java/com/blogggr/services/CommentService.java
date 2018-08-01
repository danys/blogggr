package com.blogggr.services;

import com.blogggr.dao.CommentDao;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.dto.CommentData;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.TimeUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@Service
@Transactional
public class CommentService {

  @Autowired
  private CommentDao commentDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private PostDao postDao;

  @Autowired
  private FriendDao friendDao;

  @Autowired
  private SimpleBundleMessageSource messageSource;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String COMMENT_NOT_FOUND = "CommentService.createComment.commentNotFoundException";

  public Comment createComment(long userId, CommentData commentData) {
    logger
        .debug("CommentService | createComment - userId {}, commentData: {}", userId, commentData);
    User user = userDao.findById(userId);
    if (user == null) {
      throw new ResourceNotFoundException(
          messageSource.getMessage("CommentService.createComment.userNotFoundException"));
    }
    Post post = postDao.findById(commentData.getPostId());
    if (post == null) {
      throw new ResourceNotFoundException(
          messageSource.getMessage("CommentService.createComment.postNotFoundException"));
    }
    //If post is not global then user must be friends with the poster (or be the poster)
    if (!post.getIsGlobal() && post.getUser().getUserId() != userId) {
      long posterUserID = post.getUser().getUserId();
      long smaller = (posterUserID < userId) ? posterUserID : userId;
      long bigger = (posterUserID >= userId) ? posterUserID : userId;
      Friend friendship = friendDao.getFriendByUserIds(smaller, bigger);
      if (friendship == null || friendship.getStatus().intValue() != 1) {
        throw new NotAuthorizedException(
            messageSource
                .getMessage("CommentService.createComment.authorizationRestrictionException"));
      }
    }
    Comment comment = new Comment();
    comment.setUser(user);
    comment.setPost(post);
    comment.setText(commentData.getText());
    comment.setTimestamp(TimeUtilities.getCurrentTimestamp());
    commentDao.save(comment);
    return comment;
  }

  public void updateComment(long commentId, long userId, CommentData commentData) {
    logger.debug("CommentService | updateComment - commentId: {}, userId {}, commentData: {}",
        commentId, userId, commentData);
    Comment comment = commentDao.findById(commentId);
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(COMMENT_NOT_FOUND));
    }
    if (comment.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(
          messageSource.getMessage("CommentService.updateComment.notAuthorized"));
    }
    comment.setTimestamp(TimeUtilities.getCurrentTimestamp()); //update timestamp
    comment.setText(commentData.getText());
  }

  public void deleteComment(long commentId, long userId) {
    logger.debug("CommentService | deleteComment - commentId: {}, userId {}", commentId, userId);
    Comment comment = commentDao.findById(commentId);
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(COMMENT_NOT_FOUND));
    }
    if (comment.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(
          messageSource.getMessage("CommentService.deleteComment.notAuthorized"));
    }
    commentDao.deleteById(commentId);
  }

  public Comment getCommentById(long commentId, long userId) {
    logger.debug("CommentService | getCommentById - commentId: {}, userId {}", commentId, userId);
    Comment comment = commentDao.findById(commentId);
    //Everybody can read the comment
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(COMMENT_NOT_FOUND));
    }
    return comment;
  }

  public List<Comment> getCommentsByPostId(long postId, long userId) {
    logger.debug("CommentService | getCommentsByPostId - postId: {}, userId {}", postId, userId);
    //Fetch the post first
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(
          messageSource.getMessage("CommentService.getCommentsByPostId.notFound"));
    }
    User postAuthor = post.getUser();
    //1. Comments of the post can be viewed if current session user is the owner or the post has global flag
    if (postAuthor.getUserId() == userId || post.getIsGlobal()) {
      return post.getComments();
    }
    //2. Post and comments can be viewed if the current user is friends with the poster
    long smallNum;
    long bigNum;
    smallNum = (postAuthor.getUserId() < userId) ? postAuthor.getUserId() : userId;
    bigNum = (postAuthor.getUserId() >= userId) ? postAuthor.getUserId() : userId;
    Friend friendship = friendDao.getFriendByUserIds(smallNum, bigNum);
    if (friendship == null) {
      throw new NotAuthorizedException(
          messageSource.getMessage("CommentService.getCommentsByPostId.notAuthorized"));
    }
    return post.getComments();
  }
}
