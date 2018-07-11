package com.blogggr.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.blogggr.config.AppConfig;
import com.blogggr.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Created by Daniel Sunnen on 08.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {

  @MockBean
  private UserService userService;

  @Autowired
  private WebTestClient webClient;

  private static final String baseUrl = AppConfig.BASE_URL + UsersController.USER_PATH;

  @Test
  public void confirmEmail_Normal(){
    when(userService.confirmEmail(any(Long.class), any(String.class))).thenReturn("dan@domain.com");
    webClient.get().uri(baseUrl+"/1/enable?challenge=abc").exchange().expectStatus().isOk();
  }

  @Test
  public void confirmEmail_Exception(){
    doThrow(new IllegalArgumentException("Bad user id")).when(userService).confirmEmail(0L, "abc");
    webClient.get().uri(baseUrl+"/0/enable?challenge=abc").exchange().expectStatus().isBadRequest();
  }
}
