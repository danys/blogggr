package com.blogggr.service;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.blogggr.dao.FriendDao;
import com.blogggr.dao.PostDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.PostData;
import com.blogggr.dto.PostDataUpdate;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.services.PostService;
import com.blogggr.utilities.TimeUtilities;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 04.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

  @MockBean
  private PostDao postDao;

  @MockBean
  private UserDao userDao;

  @MockBean
  private FriendDao friendDao;

  @Autowired
  private PostService postService;

  @Test
  public void createPost_Normal() {
    PostData postData = new PostData();
    postData.setTextBody("text");
    postData.setIsGlobal(false);
    postData.setTitle("title");
    User user = new User();
    user.setUserId(1L);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    doNothing().when(postDao).save(any(Post.class));
    Post dbPost = postService.createPost(1L, postData);
    assertThat(System.currentTimeMillis() - dbPost.getTimestamp().getTime()).isLessThan(10000);
    assertThat(dbPost.getTitle()).isEqualTo(postData.getTitle());
    assertThat(dbPost.getIsGlobal()).isEqualTo(postData.getIsGlobal());
    assertThat(dbPost.getTextBody()).isEqualTo(postData.getTextBody());
  }

  @Test
  public void createPost_User_Null() {
    PostData postData = new PostData();
    when(userDao.findById(any(Long.class))).thenReturn(null);
    doNothing().when(postDao).save(any(Post.class));
    try {
      postService.createPost(1L, postData);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void updatePost_Normal() {
    Post post = new Post();
    post.setPostId(1L);
    User user = new User();
    user.setUserId(1L);
    post.setUser(user);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    doNothing().when(postDao).save(any(Post.class));
    PostDataUpdate postData = new PostDataUpdate();
    postData.setTextBody("text");
    postData.setTitle("title");
    postData.setIsGlobal(true);
    Post dbPost = postService.updatePost(1L, 1L, postData);
    assertThat(dbPost.getTitle()).isEqualTo(postData.getTitle());
    assertThat(dbPost.getIsGlobal()).isEqualTo(postData.getIsGlobal());
    assertThat(dbPost.getTextBody()).isEqualTo(postData.getTextBody());
  }

  @Test
  public void updatePost_Post_Null() {
    User user = new User();
    user.setUserId(1L);
    when(postDao.findById(any(Long.class))).thenReturn(null);
    PostDataUpdate postData = new PostDataUpdate();
    try {
      postService.updatePost(1L, 1L, postData);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("Post not found");
    }
  }

  @Test
  public void updatePost_User_Not_Post_Owner() {
    Post post = new Post();
    post.setPostId(1L);
    User user = new User();
    user.setUserId(2L);
    post.setUser(user);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    PostDataUpdate postData = new PostDataUpdate();
    try {
      postService.updatePost(1L, 1L, postData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("No authorization to modify this post");
    }
  }

  @Test
  public void deletePost_Normal() {
    Post post = new Post();
    post.setPostId(1L);
    User user = new User();
    user.setUserId(1L);
    post.setUser(user);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    doNothing().when(postDao).deleteById(any(Long.class));
    postService.deletePost(1L, 1L);
  }

  @Test
  public void deletePost_Post_Null() {
    when(postDao.findById(any(Long.class))).thenReturn(null);
    try {
      postService.deletePost(1L, 1L);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage().contains("Post not found"));
    }
  }

  @Test
  public void deletePost_Not_Authorized() {
    Post post = new Post();
    post.setPostId(1L);
    User user = new User();
    user.setUserId(1L);
    post.setUser(user);
    when(postDao.findById(any(Long.class))).thenReturn(post);
    try {
      postService.deletePost(1L, 2L);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("No authorization to modify this post");
    }
  }

  @Test
  public void getPostById_User_Fetch_Own_Post() {
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setTextBody("text");
    when(postDao.findById(1L)).thenReturn(post);
    Post dbPost = postService.getPostById(1L, 1L);
    assertThat(dbPost.getTextBody()).isEqualTo("text");
  }

  @Test
  public void getPostById_Fetch_Global() {
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(true);
    post.setTextBody("text");
    when(postDao.findById(1L)).thenReturn(post);
    Post dbPost = postService.getPostById(1L, 10L);
    assertThat(dbPost.getTextBody()).isEqualTo("text");
  }

  @Test
  public void getPostById_Post_Null() {
    when(postDao.findById(1L)).thenReturn(null);
    try {
      postService.getPostById(1L, 10L);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("Post not found");
    }
  }

  @Test
  public void getPostById_Fetch_Friend_Post() {
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setTextBody("text");
    when(postDao.findById(1L)).thenReturn(post);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    Post dbPost = postService.getPostById(1L, 10L);
    assertThat(dbPost.getTextBody()).isEqualTo("text");
  }

  @Test
  public void getPostById_Not_Authorized() {
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setPostId(1L);
    post.setUser(user);
    post.setIsGlobal(false);
    post.setTextBody("text");
    when(postDao.findById(1L)).thenReturn(post);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(null);
    try {
      postService.getPostById(1L, 10L);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("No authorization to view this post");
    }
  }

  @Test
  public void getPosts_Normal(){
    User user = new User();
    user.setUserId(1L);
    user.setUserImages(new ArrayList<>());
    List<Post> posts = new ArrayList<>();
    Post post = new Post();
    post.setPostId(1L);
    post.setTextBody("text");
    post.setUser(user);
    posts.add(post);
    PageData pageData = new PageData();
    PrevNextListPage<Post> prevNextListPosts = new PrevNextListPage<>(posts, pageData);
    when(postDao.getPosts(any(PostSearchData.class), any(User.class))).thenReturn(prevNextListPosts);
    PostSearchData postSearchData = new PostSearchData();
    PrevNextListPage<Post> dbPosts = postService.getPosts(postSearchData, user);
    assertThat(dbPosts.getPageItems().size()).isEqualTo(posts.size());
    assertThat(dbPosts.getPageItems().get(0).getTextBody()).isEqualTo("text");
  }

  @Test
  public void getPosts_Text_Too_Long(){
    User user = new User();
    user.setUserId(1L);
    user.setUserImages(new ArrayList<>());
    List<Post> posts = new ArrayList<>();
    Post post = new Post();
    post.setPostId(1L);
    post.setTextBody("text123456789101213_text123456789101213_text123456789101213_text123456789101213_text123456789101213_1234567890");
    post.setUser(user);
    posts.add(post);
    PageData pageData = new PageData();
    PrevNextListPage<Post> prevNextListPosts = new PrevNextListPage<>(posts, pageData);
    when(postDao.getPosts(any(PostSearchData.class), any(User.class))).thenReturn(prevNextListPosts);
    PostSearchData postSearchData = new PostSearchData();
    PrevNextListPage<Post> dbPosts = postService.getPosts(postSearchData, user);
    assertThat(dbPosts.getPageItems().size()).isEqualTo(posts.size());
    //Test that text has been shortened
    assertThat(dbPosts.getPageItems().get(0).getTextBody()).isEqualTo("text123456789101213_text123456789101213_text123456789101213_text123456789101213_text123456789101213_...");
  }

  @Test
  public void getPosts_Data_Exception(){
    User user = new User();
    user.setUserId(1L);
    user.setUserImages(new ArrayList<>());
    doThrow(new InvalidDataAccessApiUsageException("Error getting posts")).when(postDao).getPosts(any(PostSearchData.class), any(User.class));
    PostSearchData postSearchData = new PostSearchData();
    try {
      postService.getPosts(postSearchData, user);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getMessage()).contains("Error getting posts");
    }
  }

  @Test
  public void getPostByUserAndLabel_User_Read_Own_Post(){
    User user = new User();
    user.setUserId(1L);
    Post post = new Post();
    post.setTitle("title1");
    post.setTextBody("text1");
    post.setUser(user);
    post.setIsGlobal(false);
    Comment comment = new Comment();
    comment.setTimestamp(TimeUtilities.getCurrentTimestamp());
    Comment comment2 = new Comment();
    comment2.setTimestamp(new Timestamp(System.currentTimeMillis()+10000L));
    post.setComments(new ArrayList<>(){{add(comment);add(comment2);}});
    when(postDao.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    Post dbPost = postService.getPostByUserAndLabel(1L, 10L, "title1");
    assertThat(dbPost.getTextBody()).isEqualTo("text1");
  }

  @Test
  public void getPostByUserAndLabel_Global_Post(){
    User user = new User();
    user.setUserId(2L);
    Post post = new Post();
    post.setTitle("title1");
    post.setTextBody("text1");
    post.setIsGlobal(true);
    post.setUser(user);
    post.setComments(new ArrayList<>());
    when(postDao.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    Post dbPost = postService.getPostByUserAndLabel(1L, 10L, "title1");
    assertThat(dbPost.getTextBody()).isEqualTo("text1");
  }

  @Test
  public void getPostByUserAndLabel_Friend(){
    User user = new User();
    user.setUserId(2L);
    Post post = new Post();
    post.setTitle("title1");
    post.setTextBody("text1");
    post.setUser(user);
    post.setIsGlobal(false);
    post.setComments(new ArrayList<>());
    when(postDao.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    Friend friend = new Friend();
    friend.setStatus(1);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    Post dbPost = postService.getPostByUserAndLabel(1L, 10L, "title1");
    assertThat(dbPost.getTextBody()).isEqualTo("text1");
  }

  @Test
  public void getPostByUserAndLabel_No_Friend(){
    User user = new User();
    user.setUserId(2L);
    Post post = new Post();
    post.setTitle("title1");
    post.setTextBody("text1");
    post.setUser(user);
    post.setIsGlobal(false);
    post.setComments(new ArrayList<>());
    when(postDao.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    Friend friend = new Friend();
    friend.setStatus(0);
    when(friendDao.getFriendByUserIds(any(Long.class),any(Long.class))).thenReturn(friend);
    try{
      postService.getPostByUserAndLabel(1L, 10L, "title1");
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("No authorization to view this post");
    }
  }

  @Test
  public void getPostByUserAndLabel_No_Result(){
    when(postDao.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(null);
    try{
      postService.getPostByUserAndLabel(1L, 10L, "title1");
      fail();
    }catch(ResourceNotFoundException e){
     assertThat(e.getMessage()).contains("Post not found");
    }
  }
}
