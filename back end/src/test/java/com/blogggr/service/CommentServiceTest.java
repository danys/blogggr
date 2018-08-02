package com.blogggr.service;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.blogggr.dao.CommentDao;
import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.CommentData;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 01.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @MockBean
  private CommentDao commentDao;

  @MockBean
  private UserDao userDao;

  @MockBean
  private PostDao postDao;

  @MockBean
  private FriendDao friendDao;

  @Test
  public void createComment_Normal(){
    CommentData commentData = new CommentData();
    commentData.setPostId(1L);
    commentData.setText("Comment");
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Post post = new Post();
    post.setPostId(10L);
    post.setIsGlobal(false);
    post.setUser(user2);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    when(friendDao.getFriendByUserIds(any(Long.class), any(Long.class))).thenReturn(friend);
    doNothing().when(commentDao).save(any(Comment.class));
    //Test case 1: Normal
    Comment comment = commentService.createComment(1L, commentData);
    assertThat(comment.getText()).isEqualTo("Comment");
    assertThat(comment.getUser().getUserId()).isEqualTo(1L);
    assertThat(comment.getPost().getPostId()).isEqualTo(10L);
    assertThat(comment.getTimestamp().getTime()-System.currentTimeMillis()).isLessThan(100000);
    //Test case2: No friendship
    friend.setStatus(0);
    try{
      commentService.createComment(1L, commentData);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("User must be friends with poster");
    }
    //Test case 3: global post
    post.setIsGlobal(true);
    try{
      commentService.createComment(1L, commentData);
    }catch(NotAuthorizedException e){
      fail();
    }
    //Test case 4: user comments on his own post
    post.setIsGlobal(false);
    post.setUser(user);
    try{
      commentService.createComment(1L, commentData);
    }catch(NotAuthorizedException e){
      fail();
    }
  }

  @Test
  public void createComment_Exception_User_Null(){
    CommentData commentData = new CommentData();
    commentData.setPostId(1L);
    commentData.setText("Comment");
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Post post = new Post();
    post.setPostId(10L);
    post.setIsGlobal(false);
    post.setUser(user2);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(userDao.findById(any(Long.class))).thenReturn(null);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    when(friendDao.getFriendByUserIds(any(Long.class), any(Long.class))).thenReturn(friend);
    doNothing().when(commentDao).save(any(Comment.class));
    try {
      commentService.createComment(1L, commentData);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void createComment_Exception_Post_Null(){
    CommentData commentData = new CommentData();
    commentData.setPostId(1L);
    commentData.setText("Comment");
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Post post = new Post();
    post.setPostId(10L);
    post.setIsGlobal(false);
    post.setUser(user2);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    when(postDao.findById(any(Long.class))).thenReturn(null);
    when(friendDao.getFriendByUserIds(any(Long.class), any(Long.class))).thenReturn(friend);
    doNothing().when(commentDao).save(any(Comment.class));
    try {
      commentService.createComment(1L, commentData);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Post not found");
    }
  }
}
