package com.blogggr.services;

import com.blogggr.dao.CommentDAO;
import com.blogggr.dao.CommentDao;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;
import com.blogggr.utilities.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService {

  private CommentDao commentDao;
  private UserDao userDao;
  private PostDao postDao;
  private FriendDao friendDao;

  private final String notCommentFound = "Comment not found!";

  private final Log logger = LogFactory.getLog(this.getClass());

  public CommentServiceImpl(CommentDao commentDao, UserDao userDao, PostDao postDao,
      FriendDao friendDao) {
    this.commentDao = commentDao;
    this.userDao = userDao;
    this.postDao = postDao;
    this.friendDao = friendDao;
  }

  @Override
  public Comment createComment(long userID, CommentData commentData)
      throws ResourceNotFoundException, DBException, NotAuthorizedException {
    User user = userDao.findById(userID);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    Post post = postDao.findById(commentData.getCommentId());
    if (post == null) {
      throw new ResourceNotFoundException("Post not found!");
    }
    //If post is not global then user must be friends with the poster (or be the poster)
    if (!post.getGlobal() && post.getUser().getUserId() != userID) {
      long posterUserID = post.getUser().getUserId();
      long smaller = (posterUserID < userID) ? posterUserID : userID;
      long bigger = (posterUserID >= userID) ? posterUserID : userID;
      try {
        friendDao.getFriendByUserIDs(smaller, bigger);
      } catch (ResourceNotFoundException e) {
        throw new NotAuthorizedException(
            "User must be friends with poster as post is not globally visible!");
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

  @Override
  public void updateComment(long commentID, long userID, CommentData commentData)
      throws ResourceNotFoundException, NotAuthorizedException {
    Comment comment = commentDao.findById(commentID);
    if (comment == null) {
      throw new ResourceNotFoundException(notCommentFound);
    }
    if (comment.getUser().getUserId() != userID) {
      throw new NotAuthorizedException("Not allowed to change comment!");
    }
    comment.setTimestamp(TimeUtilities.getCurrentTimestamp()); //update timestamp
    comment.setText(commentData.getText());
  }

  @Override
  public void deleteComment(long commentID, long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DBException {
    try {
      Comment comment = commentDao.findById(commentID);
      if (comment == null) {
        throw new ResourceNotFoundException(notCommentFound);
      }
      if (comment.getUser().getUserId() != userID) {
        throw new NotAuthorizedException("Not allowed to delete comment!");
      }
      commentDao.deleteById(commentID);
    } catch (Exception e) {
      throw new DBException("Database exception deleting comment!");
    }
  }

  @Override
  public Comment getCommentById(long commentID, long userID) throws ResourceNotFoundException {
    Comment comment = commentDao.findById(commentID);
    //Everybody can read the comment
    if (comment == null) {
      throw new ResourceNotFoundException(notCommentFound);
    }
    return comment;
  }

  @Override
  public List<Comment> getCommentsByPostId(long postID, long userID)
      throws ResourceNotFoundException, NotAuthorizedException, DBException {
    //Fetch the post first
    Post post = postDao.findById(postID);
    if (post == null) {
      throw new ResourceNotFoundException("Did not find post!");
    }
    User postAuthor = post.getUser();
    //1. Comments of the post can be viewed if current session user is the owner or the post has global flag
    if (postAuthor.getUserId() == userID || post.getGlobal()) {
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
      throw new NotAuthorizedException("Not allowed to view this post and its comments!");
    }
  }
}
