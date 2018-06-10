package com.blogggr.services;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.dto.PostData;
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
public class PostService {

  private PostDao postDao;
  private UserDao userDao;
  private FriendDao friendDao;

  private final String postNotFound = "Post not found!";
  private final String noModifyAuthorization = "No authorization to modify this post!";
  private final String noReadAuthorization = "No authorization to view this post!";
  private final String dbException = "Database exception!";

  private final Log logger = LogFactory.getLog(this.getClass());

  public PostService(PostDao postDao, UserDao userDao, FriendDao friendDao) {
    this.postDao = postDao;
    this.userDao = userDao;
    this.friendDao = friendDao;
  }

  public Post createPost(long userId, PostData postData) throws ResourceNotFoundException {
    User user = userDao.findById(userId);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    Post post = new Post();
    post.setUser(user);
    post.setTitle(postData.getTitle());
    post.setTextBody(postData.getTextBody());
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    post.setShortTitle(StringUtilities.compactTitle(postData.getTitle()));
    post.setIsGlobal(postData.getIsGlobal());
    postDao.save(post);
    return post;
  }

  public Post updatePost(long postID, long userId, PostData postData)
      throws ResourceNotFoundException, NotAuthorizedException {
    Post post = postDao.findById(postID);
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
    if (postData.getIsGlobal() != null) {
      post.setIsGlobal(postData.getIsGlobal());
    }
    postDao.save(post);
    return post;
  }

  //Delete a session by its primary key
  public void deletePost(long postId, long userId)
      throws ResourceNotFoundException, NotAuthorizedException {
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(postNotFound);
    }
    if (post.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(noModifyAuthorization);
    }
    postDao.deleteById(postId);
  }

  //Simple function to check that this user is friends with the poster
  private boolean isFriendOfUser(long postUserID, long userId) throws DbException {
    try {
      long smallNum, bigNum;
      smallNum = (postUserID < userId) ? postUserID : userId;
      bigNum = (postUserID >= userId) ? postUserID : userId;
      friendDao.getFriendByUserIDs(smallNum, bigNum);
      return true;
    } catch (ResourceNotFoundException e) {
      return false;
    }
  }

  public Post getPostById(long postId, long userId)
      throws ResourceNotFoundException, DbException, NotAuthorizedException {
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(postNotFound);
    }
    User postAuthor = post.getUser();
    //1. Post can be viewed if current session user is the owner or the post has global flag
    if (postAuthor.getUserId() == userId || post.getIsGlobal()) {
      return post;
    }
    //2. Post can be viewed if the current user is friends with the poster
    if (isFriendOfUser(postAuthor.getUserId(), userId)) {
      return post;
    }
    //Otherwise access denied
    throw new NotAuthorizedException(noReadAuthorization);
  }

  public PrevNextListPage<Post> getPosts(PostSearchData postSearchData, User user)
      throws DbException {
    PrevNextListPage<Post> postsPage = postDao
        .getPosts(postSearchData, user);
    List<Post> posts = postsPage.getPageItems();
    //Shorten and append ... if the post's text is too long. Load image.
    posts.forEach(post -> {
      post.getUser().getImage();
      if (post.getTextBody() != null
          && post.getTextBody().length() > AppConfig.maxPostBodyLength) {
        post.setTextBody(post.getTextBody().substring(0, AppConfig.maxPostBodyLength) + "...");
      }
    });
    return postsPage;
  }

  public Post getPostByUserAndLabel(Long userId, Long postUserID, String postShortTitle)
      throws ResourceNotFoundException, DbException, NotAuthorizedException {
    try {
      Post post = postDao.getPostByUserAndLabel(userId, postUserID, postShortTitle);
      //Order comments by date
      List<Comment> comments = post.getComments();
      Collections.sort(comments, new Comparator<Comment>() {
        @Override
        public int compare(Comment o1, Comment o2) {
          return (int) (o1.getTimestamp().getTime() - o2.getTimestamp().getTime());
        }
      });
      post.setComments(comments);
      //1. Post can be viewed if current session user is the owner or the post has global flag
      if (post.getUser().getUserId() == userId || post.getIsGlobal()) {
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
      throw new DbException(dbException, e);
    }
  }
}
