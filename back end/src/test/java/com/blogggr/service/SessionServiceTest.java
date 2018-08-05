package com.blogggr.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.entities.User;
import com.blogggr.services.SessionService;
import com.blogggr.services.SessionService.SessionDetails;
import java.io.UnsupportedEncodingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 05.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SessionServiceTest {

  @Autowired
  private SessionService sessionService;

  @Test
  public void createSession_Normal() throws UnsupportedEncodingException {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("dan@dan.com");
    SessionDetails details = sessionService.createSession(user);
    assertThat(details.getEmail()).isEqualTo("dan@dan.com");
    assertThat(details.getJwt()).isNotNull();
    assertThat(details.getExpiration()).isNotNull();
  }
}
