package com.blogggr.service;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.blogggr.dao.UserRepository;
import com.blogggr.entities.User;
import com.blogggr.services.UserService;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 08.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void confirmEmail_Normal(){
    User user = new User();
    user.setUserId(1L);
    user.setEmail("netfox@domain.com");
    user.setChallenge("abc");
    user.setStatus(0);
    Optional<User> optionalUser = Optional.of(user);
    when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);
    String email = userService.confirmEmail(1L, "abc");
    assertEquals("netfox@domain.com", email);
  }

  @Test
  public void confirmEmail_InvalidChallenge(){
    User user = new User();
    user.setUserId(1L);
    user.setEmail("netfox@domain.com");
    user.setChallenge("abc");
    user.setStatus(0);
    Optional<User> optionalUser = Optional.of(user);
    when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);
    try {
      userService.confirmEmail(1L, "ab");
      Assert.fail();
    } catch(IllegalArgumentException e){
      assertThat(e.getMessage()).contains("wrong challenge");
    }
  }

  @Test
  public void confirmEmail_UserNotFound(){
    Optional<User> optionalUser = Optional.empty();
    when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);
    try {
      userService.confirmEmail(1L, "abc");
      Assert.fail();
    } catch(IllegalArgumentException e){
      assertThat(e.getMessage()).contains("User not found");
    }
  }
}
