package com.blogggr.service;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

  @Test
  public void updateComment_Normal(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(1L);
    User user = new User();
    user.setUserId(1L);
    Comment comment = mock(Comment.class);
    when(comment.getUser()).thenReturn(user);
    ArgumentCaptor<String> textArg = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Timestamp> timeArg = ArgumentCaptor.forClass(Timestamp.class);
    when(commentDao.findById(any(Long.class))).thenReturn(comment);
    commentService.updateComment(1L,1L, commentData);
    verify(comment).setText(textArg.capture());
    verify(comment).setTimestamp(timeArg.capture());
    assertThat(textArg.getValue()).isEqualTo("comment");
    assertThat(System.currentTimeMillis()-timeArg.getValue().getTime()).isLessThan(10000);
  }

  @Test
  public void updateComment_Null_Comment(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(1L);
    User user = new User();
    user.setUserId(1L);
    when(commentDao.findById(any(Long.class))).thenReturn(null);
    try {
      commentService.updateComment(1L, 1L, commentData);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Comment not found");
    }
  }

  @Test
  public void updateComment_Unauthorized_UserId(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(2L);
    User user = new User();
    user.setUserId(1L);
    Comment comment = new Comment();
    comment.setUser(user);
    when(commentDao.findById(any(Long.class))).thenReturn(comment);
    try {
      commentService.updateComment(1L, 10L, commentData);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not allowed to change comment");
    }
  }

  @Test
  public void deleteComment_Normal(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(2L);
    User user = new User();
    user.setUserId(1L);
    Comment comment = new Comment();
    comment.setUser(user);
    when(commentDao.findById(any(Long.class))).thenReturn(comment);
    commentService.deleteComment(10L, 1L);
    verify(commentDao, times(1)).deleteById(10L);
  }

  @Test
  public void deleteComment_Comment_Null(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(2L);
    when(commentDao.findById(any(Long.class))).thenReturn(null);
    try {
      commentService.deleteComment(10L, 1L);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Comment not found");
    }
  }

  @Test
  public void deleteComment_NotAuthorized_User(){
    CommentData commentData = new CommentData();
    commentData.setText("comment");
    commentData.setPostId(2L);
    User user = new User();
    user.setUserId(1L);
    Comment comment = new Comment();
    comment.setUser(user);
    when(commentDao.findById(any(Long.class))).thenReturn(comment);
    try {
      commentService.deleteComment(10L, 10L);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not allowed to delete comment");
    }
  }

  @Test
  public void getCommentById_Normal(){
    User user = new User();
    user.setUserId(1L);
    Comment comment = new Comment();
    comment.setUser(user);
    comment.setText("commentText");
    when(commentDao.findById(any(Long.class))).thenReturn(comment);
    Comment dbComment = commentService.getCommentById(1L, 1L);
    assertThat(dbComment.getText()).isEqualTo("commentText");
  }

  @Test
  public void getCommentById_Null_Comment(){
    when(commentDao.findById(any(Long.class))).thenReturn(null);
    try {
      commentService.getCommentById(1L, 1L);
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Comment not found");
    }
  }

  @Test
  public void getCommentsByPostId_Normal(){
    Comment comment = new Comment();
    comment.setText("comment1");
    comment.setCommentId(100L);
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setComments(comments.stream().collect(Collectors.toSet()));
    when(postDao.findById(any(Long.class))).thenReturn(post);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    List<Comment> dbComments = commentService.getCommentsByPostId(1L, 1L);
    assertThat(dbComments.size()).isEqualTo(1);
    assertThat(dbComments.get(0).getText()).isEqualTo("comment1");
    assertThat(dbComments.get(0).getCommentId()).isEqualTo(100L);
  }

  @Test
  public void getCommentsByPostId_Post_Null(){
    when(postDao.findById(any(Long.class))).thenReturn(null);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    try {
      commentService.getCommentsByPostId(1L, 1L);
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Did not find post");
    }
  }

  @Test
  public void getCommentsByPostId_User_Friends_With_Poster(){
    Comment comment = new Comment();
    comment.setText("comment1");
    comment.setCommentId(100L);
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);
    User user = new User();
    user.setUserId(10L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setComments(comments.stream().collect(Collectors.toSet()));
    when(postDao.findById(any(Long.class))).thenReturn(post);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    List<Comment> dbComments = commentService.getCommentsByPostId(1L, 1L);
    assertThat(dbComments.size()).isEqualTo(1);
    assertThat(dbComments.get(0).getText()).isEqualTo("comment1");
    assertThat(dbComments.get(0).getCommentId()).isEqualTo(100L);
  }

  @Test
  public void getCommentsByPostId_User_No_Friends_With_Poster(){
    Comment comment = new Comment();
    comment.setText("comment1");
    comment.setCommentId(100L);
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);
    User user = new User();
    user.setUserId(10L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setComments(comments.stream().collect(Collectors.toSet()));
    when(postDao.findById(any(Long.class))).thenReturn(post);
    //First test case
    Friend friend = new Friend();
    friend.setStatus(0);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    try{
      commentService.getCommentsByPostId(1L, 1L);
    }catch (NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not allowed to view this post and its comments");
    }
    //Another test case
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(null);
    try{
      commentService.getCommentsByPostId(1L, 1L);
    }catch (NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not allowed to view this post and its comments");
    }
  }
}
