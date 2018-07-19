package com.blogggr.services;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.PostDataUpdate;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.dto.PostData;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.StringUtilities;
import com.blogggr.utilities.TimeUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@Service
@Transactional
public class PostService {

  @Autowired
  private PostDao postDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private FriendDao friendDao;

  @Autowired
  private SimpleBundleMessageSource messageSource;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String POST_NOT_FOUND = "PostService.postNotFound";

  public Post createPost(long userId, PostData postData) {
    logger.debug("PostService | createPost - userId: {}, postData: {}", userId, postData);
    User user = userDao.findById(userId);
    if (user == null) {
      throw new ResourceNotFoundException(messageSource.getMessage("PostService.userNotFound"));
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

  public Post updatePost(long postId, long userId, PostDataUpdate postData) {
    logger.debug("PostService | updatePost - postId: {}, userId: {}, postData: {}", userId, userId,
        postData);
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(POST_NOT_FOUND));
    }
    if (post.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("PostService.notAuthorizedModify"));
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
  public void deletePost(long postId, long userId) {
    logger.debug("PostService | deletePost - userId: {}, postData: {}", postId, userId);
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(POST_NOT_FOUND));
    }
    if (post.getUser().getUserId() != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("PostService.notAuthorizedModify"));
    }
    postDao.deleteById(postId);
  }

  //Simple function to check that this user is friends with the poster
  private boolean isFriendOfUser(long postUserID, long userId) {
    try {
      long smallNum;
      long bigNum;
      smallNum = (postUserID < userId) ? postUserID : userId;
      bigNum = (postUserID >= userId) ? postUserID : userId;
      friendDao.getFriendByUserIds(smallNum, bigNum);
      return true;
    } catch (ResourceNotFoundException e) {
      return false;
    }
  }

  public Post getPostById(long postId, long userId) {
    logger.debug("PostService | getPostById - postId: {}, userId: {}", postId, userId);
    Post post = postDao.findById(postId);
    if (post == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(POST_NOT_FOUND));
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
    throw new NotAuthorizedException(messageSource.getMessage("PostService.notAuthorizedView"));
  }

  public PrevNextListPage<Post> getPosts(PostSearchData postSearchData, User user) {
    logger.debug("PostService | getPosts - postSearchData: {}, user: {}", postSearchData, user);
    PrevNextListPage<Post> postsPage = postDao
        .getPosts(postSearchData, user);
    List<Post> posts = postsPage.getPageItems();
    //Shorten and append ... if the post's text is too long. Load image.
    posts.forEach(post -> {
      post.getUser().getImage();
      if (post.getTextBody() != null
          && post.getTextBody().length() > AppConfig.MAX_POST_BODY_LENGTH) {
        post.setTextBody(post.getTextBody().substring(0, AppConfig.MAX_POST_BODY_LENGTH) + "...");
      }
    });
    return postsPage;
  }

  public Post getPostByUserAndLabel(Long userId, Long postUserId, String postShortTitle) {
    logger.debug(
        "PostService | getPostByUserAndLabel - userId: {}, postUserId: {}, postShortTitle: {}",
        userId, postUserId, postShortTitle);
    try {
      Post post = postDao.getPostByUserAndLabel(userId, postUserId, postShortTitle);
      //Order comments by date
      List<Comment> comments = post.getComments();
      Collections.sort(comments,
          (Comment o1, Comment o2) -> (int) (o1.getTimestamp().getTime() - o2.getTimestamp()
              .getTime()));
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
      throw new NotAuthorizedException(messageSource.getMessage("PostService.notAuthorizedView"));
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(messageSource.getMessage(POST_NOT_FOUND));
    }
  }
}
