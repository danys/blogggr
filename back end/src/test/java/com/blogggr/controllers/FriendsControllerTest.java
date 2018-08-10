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
import com.blogggr.dto.CommentData;
import com.blogggr.dto.FriendDataBase;
import com.blogggr.dto.FriendDataUpdate;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.FriendPk;
import com.blogggr.entities.User;
import com.blogggr.services.FriendService;
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
 * Created by Daniel Sunnen on 09.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class FriendsControllerTest {

  @MockBean
  private FriendService friendService;

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
  public void createFriendship_Not_Authorized() throws Exception{
    FriendDataBase friendBase = new FriendDataBase();
    mvc.perform(post(BASE_URL + "/friends", friendBase)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void createFriendship_Normal() throws Exception{
    FriendDataBase friendBase = new FriendDataBase();
    friendBase.setUserId1(1L);
    friendBase.setUserId2(2L);
    Friend friend = new Friend();
    FriendPk pk = new FriendPk();
    pk.setUserOneId(1L);
    pk.setUserTwoId(2L);
    friend.setId(pk);
    when(friendService.createFriend(any(Long.class),any(FriendDataBase.class))).thenReturn(friend);
    mvc.perform(post(BASE_URL + "/friends")
        .with(user(createUserPrincipal("dan@dan.com",1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(friendBase)))
        .andExpect(status().isCreated())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"))
        .andExpect(header().string(AppConfig.LOCATION_HEADER_KEY,AppConfig.FULL_BASE_URL + "/friends/" + friend.getId().getUserOneId() + '/' + friend.getId().getUserTwoId()));
  }

  @Test
  public void updateFriendship_No_Authentication() throws Exception{
    FriendDataUpdate friendData = new FriendDataUpdate();
    mvc.perform(put(BASE_URL + "/friends/1/2")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void updateFriendship_Normal() throws Exception{
    FriendDataUpdate friendData = new FriendDataUpdate();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    friendData.setAction(1);
    doNothing().when(friendService).updateFriend(any(Long.class),any(Long.class),any(Long.class),any(FriendDataUpdate.class));
    mvc.perform(put(BASE_URL + "/friends/1/2")
        .with(user(createUserPrincipal("dan@dan.com",1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(friendData)))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }

  @Test
  public void getFriends_Not_Authorized() throws Exception{
    mvc.perform(get(BASE_URL + "/friends"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getFriends_Normal() throws Exception{
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setUserId(10L);
    user.setEmail("dany@dan.com");
    user.setFirstName("daniel");
    user.setLastName("Müller");
    users.add(user);
    when(friendService.getFriends(any(Long.class))).thenReturn(users);
    mvc.perform(get(BASE_URL + "/friends")
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': [{'userId': 10, 'email': 'dany@dan.com', 'firstName': 'daniel', 'lastName': 'Müller'}]}"));
  }

  @Test
  public void getFriendship_Not_Authorized() throws Exception{
    mvc.perform(get(BASE_URL + "/friends/1/2"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getFriendship_Normal() throws Exception{
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setStatus(1);
    when(friendService.getFriend(any(Long.class),any(Long.class),any(Long.class))).thenReturn(friend);
    mvc.perform(get(BASE_URL + "/friends/1/2")
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk());
  }

  @Test
  public void deleteFriend_Not_Authorized() throws Exception{
    mvc.perform(delete(BASE_URL + "/friends/1/2"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void deleteFriend_Normal() throws Exception{
    doNothing().when(friendService).deleteFriend(any(Long.class),any(Long.class),any(Long.class));
    mvc.perform(delete(BASE_URL + "/friends/1/2")
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }
}
