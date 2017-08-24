package com.blogggr.services;

import com.blogggr.dao.FriendDAO;
import com.blogggr.dao.PostDAO;
import com.blogggr.dao.PostDAOImpl;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.PageData;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.requestdata.PostData;
import com.blogggr.utilities.StringUtilities;
import com.blogggr.utilities.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl implements PostService {

  private PostDAO postDAO;
  private UserDAO userDAO;
  private FriendDAO friendDAO;

  private final String postNotFound = "Post not found!";
  private final String noModifyAuthorization = "No authorization to modify this post!";
  private final String noReadAuthorization = "No authorization to view this post!";
  private final String dbException = "Database exception!";

  private final Log logger = LogFactory.getLog(this.getClass());

  public PostServiceImpl(PostDAO postDAO, UserDAO userDAO, FriendDAO friendDAO) {
    this.postDAO = postDAO;
    this.userDAO = userDAO;
    this.friendDAO = friendDAO;
  }


  @Override
  public Post createPost(long userId, PostData postData) throws ResourceNotFoundException {
    User user = userDAO.findById(userId);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    Post post = new Post();
    post.setUser(user);
    post.setTitle(postData.getTitle());
    post.setTextBody(postData.getTextBody());
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    post.setShortTitle(StringUtilities.compactTitle(postData.getTitle()));
    post.setGlobal(postData.getGlobal());
    postDAO.save(post);
    return post;
  }

  @Override
  public Post updatePost(long postID, long userId, PostData postData)
      throws ResourceNotFoundException, NotAuthorizedException {
    Post post = postDAO.findById(postID);
    if (post == null) {
      throw new ResourceNotFoundException(postNotFound);
    }
    if (post.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(noModifyAuthorization);
    }
    //Update timestamp
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    if (postData.getTextBody() != null) {
      post.setTextBody(postData.getTextBody());
    }
    if (postData.getTitle() != null) {
      post.setTitle(postData.getTitle());
      post.setShortTitle(StringUtilities.compactTitle(postData.getTitle()));
    }
    if (postData.getGlobal() != null) {
      post.setGlobal(postData.getGlobal());
    }
    postDAO.save(post);
    return post;
  }

  //Delete a session by its primary key
  @Override
  public void deletePost(long postId, long userId)
      throws ResourceNotFoundException, NotAuthorizedException {
    Post post = postDAO.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(postNotFound);
    }
    if (post.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(noModifyAuthorization);
    }
    postDAO.deleteById(postId);
  }

  //Simple function to check that this user is friends with the poster
  private boolean isFriendOfUser(long postUserID, long userId) throws DBException {
    try {
      long smallNum, bigNum;
      smallNum = (postUserID < userId) ? postUserID : userId;
      bigNum = (postUserID >= userId) ? postUserID : userId;
      friendDAO.getFriendByUserIDs(smallNum, bigNum);
      return true;
    } catch (ResourceNotFoundException e) {
      return false;
    }
  }

  @Override
  public Post getPostById(long postId, long userId)
      throws ResourceNotFoundException, DBException, NotAuthorizedException {
    Post post = postDAO.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(postNotFound);
    }
    User postAuthor = post.getUser();
    //1. Post can be viewed if current session user is the owner or the post has global flag
    if (postAuthor.getUserId() == userId || post.getGlobal()) {
      return post;
    }
    //2. Post can be viewed if the current user is friends with the poster
    if (isFriendOfUser(postAuthor.getUserId(), userId)) {
      return post;
    }
    //Otherwise access denied
    throw new NotAuthorizedException(noReadAuthorization);
  }

  @Override
  public PrevNextListPage<Post> getPosts(long userId, Long postUserID, String title,
      PostDAOImpl.Visibility visibility, Long before, Long after, Integer limit)
      throws ResourceNotFoundException, DBException {
    PrevNextListPage<Post> postsPage = postDAO
        .getPosts(userId, postUserID, title, visibility, before, after, limit);
    return postsPage;
  }

  @Override
  public Post getPostByUserAndLabel(Long userId, Long postUserID, String postShortTitle)
      throws ResourceNotFoundException, DBException, NotAuthorizedException {
    try {
      Post post = postDAO.getPostByUserAndLabel(userId, postUserID, postShortTitle);
      //Order comments by date
      List<Comment> comments = post.getComments();
      Collections.sort(comments, new Comparator<Comment>() {
        @Override
        public int compare(Comment o1, Comment o2) {
          return (int) (o1.getRealTimestamp().getTime() - o2.getRealTimestamp().getTime());
        }
      });
      post.setComments(comments);
      //1. Post can be viewed if current session user is the owner or the post has global flag
      if (post.getUser().getUserId() == userId || post.getGlobal()) {
        return post;
      }
      //2. Post can be viewed if the current user is friends with the poster
      if (isFriendOfUser(post.getUser().getUserId(), userId)) {
        return post;
      }
      //Otherwise access denied
      throw new NotAuthorizedException(noReadAuthorization);
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(postNotFound);
    } catch (RuntimeException e) {
      throw new DBException(dbException, e);
    }
  }
}
