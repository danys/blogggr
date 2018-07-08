package com.blogggr.services;

import com.blogggr.dao.CommentDao;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.entities.Comment;
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

  public Comment createComment(long userID, CommentData commentData) {
    User user = userDao.findById(userID);
    if (user == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.createComment.userNotFoundException"));
    }
    Post post = postDao.findById(commentData.getPostId());
    if (post == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.createComment.postNotFoundException"));
    }
    //If post is not global then user must be friends with the poster (or be the poster)
    if (!post.getIsGlobal() && post.getUser().getUserId() != userID) {
      long posterUserID = post.getUser().getUserId();
      long smaller = (posterUserID < userID) ? posterUserID : userID;
      long bigger = (posterUserID >= userID) ? posterUserID : userID;
      try {
        friendDao.getFriendByUserIDs(smaller, bigger);
      } catch (ResourceNotFoundException e) {
        throw new NotAuthorizedException(
            messageSource.getMessage("CommentService.createComment.authorizationRestrictionException"));
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

  public void updateComment(long commentID, long userID, CommentData commentData) {
    Comment comment = commentDao.findById(commentID);
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.createComment.commentNotFoundException"));
    }
    if (comment.getUser().getUserId() != userID) {
      throw new NotAuthorizedException(messageSource.getMessage("CommentService.updateComment.notAuthorized"));
    }
    comment.setTimestamp(TimeUtilities.getCurrentTimestamp()); //update timestamp
    comment.setText(commentData.getText());
  }

  public void deleteComment(long commentID, long userID) {
    Comment comment = commentDao.findById(commentID);
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.createComment.commentNotFoundException"));
    }
    if (comment.getUser().getUserId() != userID) {
      throw new NotAuthorizedException(messageSource.getMessage("CommentService.deleteComment.notAuthorized"));
    }
    commentDao.deleteById(commentID);
  }

  public Comment getCommentById(long commentID, long userID) {
    Comment comment = commentDao.findById(commentID);
    //Everybody can read the comment
    if (comment == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.createComment.commentNotFoundException"));
    }
    return comment;
  }

  public List<Comment> getCommentsByPostId(long postID, long userID) {
    //Fetch the post first
    Post post = postDao.findById(postID);
    if (post == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("CommentService.getCommentsByPostId.notFound"));
    }
    User postAuthor = post.getUser();
    //1. Comments of the post can be viewed if current session user is the owner or the post has global flag
    if (postAuthor.getUserId() == userID || post.getIsGlobal()) {
      return post.getComments();
    }
    //2. Post and comments can be viewed if the current user is friends with the poster
    long smallNum, bigNum;
    smallNum = (postAuthor.getUserId() < userID) ? postAuthor.getUserId() : userID;
    bigNum = (postAuthor.getUserId() >= userID) ? postAuthor.getUserId() : userID;
    try {
      friendDao.getFriendByUserIDs(smallNum, bigNum);
      return post.getComments();
    } catch (ResourceNotFoundException e) {
      throw new NotAuthorizedException(messageSource.getMessage("CommentService.getCommentsByPostId.notAuthorized"));
    }
  }
}
