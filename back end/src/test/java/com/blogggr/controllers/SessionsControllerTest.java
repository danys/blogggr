package com.blogggr.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.UserEnums.Lang;
import com.blogggr.dto.UserEnums.Sex;
import com.blogggr.dto.UserPostData;
import com.blogggr.entities.User;
import com.blogggr.services.SessionService;
import com.blogggr.services.SessionService.SessionDetails;
import com.blogggr.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Daniel Sunnen on 12.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class SessionsControllerTest {

  @Autowired
  private UserService userService;

  @MockBean
  private SessionService sessionService;

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
  @Transactional
  public void createSession_Forbidden_User_Not_Activated() throws Exception {
    UserPostData userPostData = new UserPostData();
    userPostData.setEmail("dan@dan.com");
    userPostData.setEmailRepeat("dan@dan.com");
    userPostData.setFirstName("Dan");
    userPostData.setLastName("Sun");
    userPostData.setPassword("dan");
    userPostData.setPasswordRepeat("dan");
    userPostData.setSex(Sex.M);
    userPostData.setLang(Lang.DE);
    userService.createUser(userPostData);
    SessionDetails sessionDetails = new SessionDetails();
    sessionDetails.setEmail("dan@dan.com");
    sessionDetails.setExpiration(ZonedDateTime.of(2000,1,1,0,0,0,0,AppConfig.LUXEMBOURG_ZONE_ID));
    when(sessionService.createSession(any(User.class))).thenReturn(sessionDetails);
    mvc.perform(post(BASE_URL + "/sessions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"dan@dan.com\",\"password\":\"dan\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  public void createSession_Normal() throws Exception {
    UserPostData userPostData = new UserPostData();
    userPostData.setEmail("dan@dan.com");
    userPostData.setEmailRepeat("dan@dan.com");
    userPostData.setFirstName("Dan");
    userPostData.setLastName("Sun");
    userPostData.setPassword("dan");
    userPostData.setPasswordRepeat("dan");
    userPostData.setSex(Sex.M);
    userPostData.setLang(Lang.DE);
    User user = userService.createUser(userPostData);
    userService.confirmEmail(user.getUserId(), user.getChallenge());
    SessionDetails sessionDetails = new SessionDetails();
    sessionDetails.setEmail("dan@dan.com");
    sessionDetails.setExpiration(ZonedDateTime.of(2000,1,1,0,0,0,0,AppConfig.LUXEMBOURG_ZONE_ID));
    when(sessionService.createSession(any(User.class))).thenReturn(sessionDetails);
    mvc.perform(post(BASE_URL + "/sessions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"dan@dan.com\",\"password\":\"dan\"}"))
        .andExpect(status().isCreated());
  }
}
