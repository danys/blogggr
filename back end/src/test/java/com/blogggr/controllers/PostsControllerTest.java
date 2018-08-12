package com.blogggr.controllers;

import static com.blogggr.controllers.CommentsControllerTest.createUserPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.PostDao.Visibility;
import com.blogggr.dto.FriendDataBase;
import com.blogggr.dto.PostData;
import com.blogggr.dto.PostDataUpdate;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.FriendPk;
import com.blogggr.entities.Post;
import com.blogggr.entities.PostImage;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.services.PostService;
import com.blogggr.utilities.TimeUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Daniel Sunnen on 10.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class PostsControllerTest {

  @MockBean
  private PostService postService;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private static final String BASE_URL = AppConfig.BASE_URL;

  private ObjectMapper mapper;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    mapper = new ObjectMapper();
  }

  @Test
  public void createPost_Not_Authorized() throws Exception{
    PostData postData = new PostData();
    postData.setTitle("title");
    postData.setTextBody("bodybodybodybody");
    postData.setIsGlobal(true);
    mvc.perform(post(BASE_URL + "/posts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(postData)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void createPost_Normal() throws Exception {
    PostData postData = new PostData();
    postData.setTitle("title");
    postData.setTextBody("bodybodybodybody");
    postData.setIsGlobal(true);
    Post post = new Post();
    post.setPostId(1000L);
    when(postService.createPost(any(Long.class), any(PostData.class))).thenReturn(post);
    mvc.perform(post(BASE_URL + "/posts")
        .with(user(createUserPrincipal("dan@dan.com", 1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(postData)))
        .andExpect(status().isCreated())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"))
        .andExpect(header().string(AppConfig.LOCATION_HEADER_KEY,
            AppConfig.FULL_BASE_URL + "/posts/" + post.getPostId()));
  }

  @Test
  public void updatePost_Not_Authorized() throws Exception{
    PostDataUpdate postData = new PostDataUpdate();
    postData.setTitle("title");
    postData.setTextBody("bodybodybodybody");
    postData.setIsGlobal(true);
    mvc.perform(put(BASE_URL + "/posts/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(postData)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void updatePost_Normal() throws Exception {
    PostDataUpdate postData = new PostDataUpdate();
    postData.setTitle("title");
    postData.setTextBody("bodybodybodybody");
    postData.setIsGlobal(true);
    when(postService.updatePost(any(Long.class),any(Long.class),any(PostDataUpdate.class))).thenReturn(null);
    mvc.perform(put(BASE_URL + "/posts/1")
        .with(user(createUserPrincipal("dan@dan.com", 1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(postData)))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }

  @Test
  public void deletePost_Not_Authorized() throws Exception{
    mvc.perform(delete(BASE_URL + "/posts/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void deletePost_Normal() throws Exception {
    doNothing().when(postService).deletePost(any(Long.class),any(Long.class));
    mvc.perform(delete(BASE_URL + "/posts/1")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }

  @Test
  public void getPost_Not_Authorized() throws Exception{
    mvc.perform(get(BASE_URL + "/posts/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getPost_Normal() throws Exception {
    Post post = new Post();
    post.setPostId(1L);
    post.setTitle("title");
    when(postService.getPostById(any(Long.class), any(Long.class))).thenReturn(post);
    mvc.perform(get(BASE_URL + "/posts/1")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getPostBy_Not_Authorized() throws Exception{
    mvc.perform(get(BASE_URL + "/users/1/posts/blabla")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getPostBy_Normal() throws Exception {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("email");
    user.setFirstName("first");
    user.setLastName("last");
    List<Comment> comments = new ArrayList<>();
    List<PostImage> userImages = new ArrayList<>();
    Comment comment = new Comment();
    UserImage userImage = new UserImage();
    //comments.add(comment);
    //userImages.add(userImage);
    Post post = new Post();
    post.setPostId(1L);
    post.setTitle("title");
    post.setShortTitle("shortTitle");
    post.setTextBody("textBody");
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    post.setComments(comments);
    post.setPostImages(userImages);
    post.setUser(user);
    when(postService.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    mvc.perform(get(BASE_URL + "/users/1/posts/blablabla")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getPostByException_Normal() throws Exception {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("email");
    user.setFirstName("first");
    user.setLastName("last");
    List<Comment> comments = new ArrayList<>();
    List<PostImage> userImages = new ArrayList<>();
    Comment comment = new Comment();
    UserImage userImage = new UserImage();
    //comments.add(comment);
    //userImages.add(userImage);
    Post post = new Post();
    post.setPostId(1L);
    post.setTitle("title");
    post.setShortTitle("shortTitle");
    post.setTextBody("textBody");
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    post.setComments(comments);
    post.setPostImages(userImages);
    post.setUser(user);
    when(postService.getPostByUserAndLabel(any(Long.class), any(Long.class), any(String.class))).thenReturn(post);
    mvc.perform(get(BASE_URL + "/users/1/posts/bl")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getPostSearchData_Not_Authorized() throws Exception{
    PostSearchData postSearchData = new PostSearchData();
    mvc.perform(get(BASE_URL + "/posts", postSearchData)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json(
            "{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getPostSearch_Normal() throws Exception {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("email");
    user.setFirstName("first");
    user.setLastName("last");
    Post post = new Post();
    post.setPostId(1L);
    post.setTitle("title");
    post.setShortTitle("shortTitle");
    post.setTextBody("textBody");
    post.setTimestamp(TimeUtilities.getCurrentTimestamp());
    post.setComments(new ArrayList<>());
    post.setPostImages(new ArrayList<>());
    post.setUser(user);
    List<Post> posts = new ArrayList<>();
    posts.add(post);
    PrevNextListPage<Post> page = new PrevNextListPage<>(posts, new PageData());
    when(postService
        .getPosts(any(PostSearchData.class), any(User.class))).thenReturn(page);
    mvc.perform(get(BASE_URL + "/posts")
        .param("title", "title")
        .param("visibility", "ALL")
        .param("maxRecordsCount", "10")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }
}
