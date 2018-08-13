package com.blogggr.service;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserRepository;
import com.blogggr.dto.SimpleUserSearchData;
import com.blogggr.dto.UserPostData;
import com.blogggr.dto.UserPutData;
import com.blogggr.dto.UserSearchData;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PageMetaData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @MockBean
  private UserDao userDao;

  private static final String PASSWORD_HASH = "$2a$11$PDQ5CYdOtW/t1oCu.2ZoV.RcDY5Hhx2x4FFRbRN9AvT5hMaXUIdAO"; //hash of plaintext 'password'

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void confirmEmail_Normal() {
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
  public void confirmEmail_InvalidChallenge() {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("netfox@domain.com");
    user.setChallenge("abc");
    user.setStatus(0);
    Optional<User> optionalUser = Optional.of(user);
    when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);
    try {
      userService.confirmEmail(1L, "ab");
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("wrong challenge");
    }
  }

  @Test
  public void confirmEmail_UserNotFound() {
    Optional<User> optionalUser = Optional.empty();
    when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);
    try {
      userService.confirmEmail(1L, "abc");
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void loadUserByUsername_Normal() {
    User user = new User();
    user.setUserId(1L);
    user.setEmail("dan@dan.com");
    when(userRepository.findByEmail(any(String.class))).thenReturn(user);
    UserDetails userDetails = userService.loadUserByUsername("dan");
    assertThat(userDetails.getUsername()).isEqualTo("dan@dan.com");
  }

  @Test
  public void loadUserByUsername_User_Null() {
    when(userRepository.findByEmail(any(String.class))).thenReturn(null);
    try {
      userService.loadUserByUsername("dan");
      fail();
    } catch (UsernameNotFoundException e) {
      assertThat(e.getMessage()).contains("dan not found");
    }
  }

  @Test
  public void getUserById_Normal() {
    User user = new User();
    user.setEmail("dan@dan.com");
    when(userDao.findById(any(Long.class))).thenReturn(user);
    assertThat(userService.getUserById(1L).getEmail()).isEqualTo("dan@dan.com");
  }

  @Test
  public void getUserById_User_Null() {
    when(userDao.findById(any(Long.class))).thenReturn(null);
    try {
      userService.getUserById(1L);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void getUserByIdWithImages_Normal() {
    User user = new User();
    user.setEmail("dan@dan.com");
    List<UserImage> userImages = new ArrayList<>();
    UserImage userImage = new UserImage();
    userImage.setName("daniel");
    userImages.add(userImage);
    user.setUserImages(userImages);
    when(userDao.findByIdWithImages(any(Long.class))).thenReturn(user);
    User dbUser = userService.getUserByIdWithImages(1L);
    assertThat(dbUser.getEmail()).isEqualTo("dan@dan.com");
    assertThat(dbUser.getUserImages().size()).isEqualTo(1);
    assertThat(dbUser.getUserImages().get(0).getName()).isEqualTo("daniel");
  }

  @Test
  public void getUserByIdWithImages_User_Null() {
    when(userDao.findByIdWithImages(any(Long.class))).thenReturn(null);
    try {
      userService.getUserByIdWithImages(1L);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void getUserByEmail_Normal() {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setUserId(100L);
    when(userRepository.findByEmail(any(String.class))).thenReturn(user);
    assertThat(userService.getUserByEmail("dan@dan.com").getUserId()).isEqualTo(100);
  }

  @Test
  public void getUserByEmail_User_Null() {
    when(userRepository.findByEmail(any(String.class))).thenReturn(null);
    try {
      userService.getUserByEmail("dan@dan.com");
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void createUser_Normal() {
    UserPostData userData = new UserPostData();
    userData.setEmail("dan@dan.com");
    userData.setEmailRepeat("dan@dan.com");
    userData.setPassword("password");
    userData.setPasswordRepeat("password");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    User dbUser = userService.createUser(userData);
    assertThat(System.currentTimeMillis() - dbUser.getLastChange().getTime()).isLessThan(10000);
    assertThat(dbUser.getStatus()).isEqualTo(0);
    assertThat(dbUser.getChallenge()).isNotNull();
    assertThat(dbUser.getEmail()).isEqualTo("dan@dan.com");
  }

  @Test
  public void createUser_Email_Mismatch() {
    UserPostData userData = new UserPostData();
    userData.setEmail("dan@dany.com");
    userData.setEmailRepeat("dan@dan.com");
    userData.setPassword("password");
    userData.setPasswordRepeat("password");
    try {
      userService.createUser(userData);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("The two e-mail fields must have the same value");
    }
  }

  @Test
  public void createUser_Password_Mismatch() {
    UserPostData userData = new UserPostData();
    userData.setEmail("dan@dan.com");
    userData.setEmailRepeat("dan@dan.com");
    userData.setPassword("passwor");
    userData.setPasswordRepeat("password");
    try {
      userService.createUser(userData);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("The two password fields must have the same value");
    }
  }

  @Test
  public void updateUser_Normal() {
    //Test case 1
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setFirstName("daniel");
    user.setLastName("sun");
    user.setUserId(1L);
    user.setPasswordHash(PASSWORD_HASH);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    UserPutData userData = new UserPutData();
    userData.setFirstName("dan");
    userData.setLastName("su");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    User dbUser = userService.updateUser(1L, 1L, userData);
    assertThat(dbUser.getFirstName()).isEqualTo("dan");
    assertThat(dbUser.getLastName()).isEqualTo("su");
    //Test case 2: change password
    when(userDao.findById(any(Long.class))).thenReturn(user);
    userData = new UserPutData();
    userData.setOldPassword("password");
    userData.setPassword("newPassword");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    dbUser = userService.updateUser(1L, 1L, userData);
    assertThat(passwordEncoder.matches("newPassword", dbUser.getPasswordHash())).isTrue();
  }

  @Test
  public void updateUser_User_Null() {
    when(userDao.findById(any(Long.class))).thenReturn(null);
    try {
      userService.updateUser(1L, 1L, null);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void updateUser_Not_Authorized() {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setFirstName("daniel");
    user.setLastName("sun");
    user.setUserId(2L);
    when(userDao.findById(2L)).thenReturn(user);
    try {
      userService.updateUser(2L, 1L, null);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Not authorized to change this user");
    }
  }

  @Test
  public void updateUser_Wrong_Old_Password() {
    //Test case 1
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setFirstName("daniel");
    user.setLastName("sun");
    user.setPasswordHash(
        "$2a$11$PDQ5CYdOtW/t1oCu.2ZoV.RcDY5Hhx2x4FFRbRN9AvT5hMaXUIdAO"); //is the hash of the plaintext 'password'
    user.setUserId(1L);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    UserPutData userData = new UserPutData();
    userData.setFirstName("dan");
    userData.setLastName("su");
    userData.setOldPassword("wrongPassword");
    userData.setPassword("newPassword");
    try {
      userService.updateUser(1L, 1L, userData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Old password is wrong");
    }
    //Test case 2: no new password provided
    userData.setPassword(null);
    try {
      userService.updateUser(1L, 1L, userData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Old password is wrong");
    }
  }

  @Test
  public void updateUser_New_Password_Without_Old_Password() {
    User user = new User();
    user.setEmail("dan@dan.com");
    user.setFirstName("daniel");
    user.setLastName("sun");
    user.setPasswordHash(
        "$2a$11$PDQ5CYdOtW/t1oCu.2ZoV.RcDY5Hhx2x4FFRbRN9AvT5hMaXUIdAO"); //is the hash of the plaintext 'password'
    user.setUserId(1L);
    when(userDao.findById(any(Long.class))).thenReturn(user);
    UserPutData userData = new UserPutData();
    userData.setFirstName("dan");
    userData.setLastName("su");
    userData.setPassword("newPassword");
    try {
      userService.updateUser(1L, 1L, userData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Old password must be provided");
    }
  }

  @Test
  public void getUsers_Normal() {
    SimpleUserSearchData searchData = new SimpleUserSearchData();
    searchData.setSearchString("user1");
    searchData.setLimit(10);
    searchData.setPageNumber(1);
    User user1 = new User();
    user1.setEmail("user1@domain.com");
    List<User> users = new ArrayList<>();
    users.add(user1);
    RandomAccessListPage<User> usersPage = new RandomAccessListPage<>(users, new PageMetaData());
    when(userDao.getUsers(any(String.class), any(Integer.class), any(Integer.class)))
        .thenReturn(usersPage);
    RandomAccessListPage<User> page = userService.getUsers(searchData);
    assertThat(page.getPageItems().size()).isEqualTo(1);
    assertThat(page.getPageItems().get(0).getEmail()).isEqualTo("user1@domain.com");
  }

  @Test
  public void getUsersBySearchTerms_Normal() {
    UserSearchData searchData = new UserSearchData();
    User user1 = new User();
    user1.setEmail("user1@domain.com");
    List<User> users = new ArrayList<>();
    users.add(user1);
    PrevNextListPage<User> pageUsers = new PrevNextListPage<>(users, new PageData());
    when(userDao.getUsersBySearchTerms(any(UserSearchData.class))).thenReturn(pageUsers);
    PrevNextListPage<User> dbUsersPage = userService.getUsersBySearchTerms(searchData);
    assertThat(dbUsersPage.getPageItems().size()).isEqualTo(1);
    assertThat(dbUsersPage.getPageItems().get(0).getEmail()).isEqualTo("user1@domain.com");
  }
}
