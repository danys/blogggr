package com.blogggr.controllers;

import static com.blogggr.controllers.CommentsControllerTest.createUserPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.PostSearchData;
import com.blogggr.dto.SimpleUserSearchData;
import com.blogggr.dto.UserEnums.Lang;
import com.blogggr.dto.UserEnums.Sex;
import com.blogggr.dto.UserPostData;
import com.blogggr.dto.UserPutData;
import com.blogggr.dto.UserSearchData;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PageMetaData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.services.EmailService;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Daniel Sunnen on 13.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class UsersControllerTest {

  @MockBean
  private UserService userService;

  @MockBean
  private EmailService emailService;

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
  public void getUsers_Not_Authorized() throws Exception {
    mvc.perform(get(BASE_URL + "/users")
        .param("firstName", "Dan")
        .param("lastName", "Sun")
        .param("email", "dan@dan.com")
        .param("maxRecordsCount", "10")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUsers_Normal() throws Exception {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setLastName("Sun");
    user.setFirstName("Dan");
    user.setUserId(10L);
    user.setImage(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    PrevNextListPage<User> page = new PrevNextListPage<>(users, new PageData());
    when(userService.getUsersBySearchTerms(any(UserSearchData.class))).thenReturn(page);
    mvc.perform(get(BASE_URL + "/users")
        .param("firstName", "Dan")
        .param("lastName", "Sun")
        .param("email", "dan@dan.com")
        .param("maxRecordsCount", "10")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getUsers_Before_After_Not_Null() throws Exception {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setLastName("Sun");
    user.setFirstName("Dan");
    user.setUserId(10L);
    user.setImage(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    PrevNextListPage<User> page = new PrevNextListPage<>(users, new PageData());
    when(userService.getUsersBySearchTerms(any(UserSearchData.class))).thenReturn(page);
    mvc.perform(get(BASE_URL + "/users")
        .param("firstName", "Dan")
        .param("lastName", "Sun")
        .param("email", "dan@dan.com")
        .param("maxRecordsCount", "10")
        .param("before", "10")
        .param("after", "2")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getUsersBySearchTerm_Not_Authorized() throws Exception {
    mvc.perform(get(BASE_URL + "/users")
        .param("searchString", "Dan")
        .param("limit", "10")
        .param("pageNumber", "1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUsersBySearchTerm_Normal() throws Exception {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setLastName("Sun");
    user.setFirstName("Dan");
    user.setUserId(1L);
    List<User> list = new ArrayList<>();
    list.add(user);
    when(userService.getUsers(any(SimpleUserSearchData.class))).thenReturn(new RandomAccessListPage<>(list, new PageMetaData()));
    mvc.perform(get(BASE_URL + "/users")
        .param("searchString", "Dan")
        .param("limit", "10")
        .param("pageNumber", "1")
        .contentType(MediaType.APPLICATION_JSON)
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getCurrentUser_Not_Authorized() throws Exception {
    mvc.perform(get(BASE_URL + "/users/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUserMe_Normal() throws Exception {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setLastName("Sun");
    user.setFirstName("Dan");
    user.setUserId(1L);
    when(userService.getUserByIdWithImages(any(Long.class))).thenReturn(user);
    mvc.perform(get(BASE_URL + "/users/me")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getUser_Not_Authorized() throws Exception {
    mvc.perform(get(BASE_URL + "/users/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUserPosts_Not_Authorized() throws Exception {
    mvc.perform(get(BASE_URL + "/users/1/posts")
        .param("maxRecordsCount", "10"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUser_Normal() throws Exception {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setLastName("Sun");
    user.setFirstName("Dan");
    user.setUserId(1L);
    when(userService.getUserByIdWithImages(any(Long.class))).thenReturn(user);
    mvc.perform(get(BASE_URL + "/users/1")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void getUserPosts_Normal() throws Exception {
    Post post = new Post();
    post.setPostId(1L);
    post.setShortTitle("titleshort");
    post.setTitle("title");
    post.setTextBody("text");
    List<Post> posts = new ArrayList<>();
    posts.add(post);
    PrevNextListPage<Post> page = new PrevNextListPage<Post>(posts, new PageData());
    when(postService
        .getPosts(any(PostSearchData.class), any(User.class))).thenReturn(page);
    mvc.perform(get(BASE_URL + "/users/1/posts")
        .param("maxRecordsCount", "10")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void createUser_Normal() throws Exception {
    UserPostData userData = new UserPostData();
    userData.setFirstName("Dan");
    userData.setLastName("Sun");
    userData.setEmailRepeat("dan@dan.com");
    userData.setEmail("dan@dan.com");
    userData.setPasswordRepeat("password");
    userData.setPassword("password");
    userData.setSex(Sex.M);
    userData.setLang(Lang.DE);
    User user = new User();
    user.setFirstName("Dan");
    user.setLastName("Sun");
    user.setEmail("dan@dan.com");
    when(userService.createUser(any(UserPostData.class))).thenReturn(user);
    doNothing().when(emailService)
        .sendSimpleMessage(any(String.class), any(String.class), any(String.class));
    mvc.perform(post(BASE_URL + "/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(userData))
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isCreated())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"))
        .andExpect(header().string(AppConfig.LOCATION_HEADER_KEY,
            AppConfig.FULL_BASE_URL + "/users/" + user.getUserId()));
  }

  @Test
  public void createUser_User_Exists_Already() throws Exception {
    UserPostData userData = new UserPostData();
    userData.setFirstName("Dan");
    userData.setLastName("Sun");
    userData.setEmailRepeat("dan@dan.com");
    userData.setEmail("dan@dan.com");
    userData.setPasswordRepeat("password");
    userData.setPassword("password");
    userData.setSex(Sex.M);
    userData.setLang(Lang.DE);
    User user = new User();
    user.setFirstName("Dan");
    user.setLastName("Sun");
    user.setEmail("dan@dan.com");
    DataIntegrityViolationException ex = new DataIntegrityViolationException("User exists");
    doThrow(ex).when(userService).createUser(any(UserPostData.class));
    doNothing().when(emailService)
        .sendSimpleMessage(any(String.class), any(String.class), any(String.class));
    mvc.perform(post(BASE_URL + "/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(userData))
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateUser_Not_Authorized() throws Exception {
    UserPutData userPutData = new UserPutData();
    mvc.perform(put(BASE_URL + "/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(userPutData)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void updateUser_Normal() throws Exception {
    UserPutData userPutData = new UserPutData();
    User user = new User();
    user.setFirstName("Dan");
    user.setLastName("Sun");
    user.setEmail("dan@dan.com");
    when(userService.updateUser(any(Long.class), any(Long.class), any(UserPutData.class)))
        .thenReturn(user);
    mvc.perform(put(BASE_URL + "/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(userPutData))
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }
}
