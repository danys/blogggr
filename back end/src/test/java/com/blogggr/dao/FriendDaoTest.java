package com.blogggr.dao;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.dto.UserEnums;
import com.blogggr.dto.UserEnums.Sex;
import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 19.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FriendDaoTest {

  @Autowired
  private FriendDao friendDao;

  @Autowired
  private UserDao userDao;

  private User createUser(String firstName, String lastName, String email, int status){
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setPasswordHash("hash");
    user.setChallenge("challenge");
    user.setStatus(status);
    user.setLastChange(Timestamp.valueOf(LocalDateTime.now()));
    user.setSex(Sex.M);
    user.setLang(UserEnums.Lang.DE);
    return user;
  }

  private Friend createFriend(User user1, User user2, int status){
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setLastActionTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    friend.setStatus(status);
    return friend;
  }

  @Test
  @Transactional
  public void getUserFriends_Normal(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    assertThat(user1.getUserId()).isNotNull();
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    assertThat(user2.getUserId()).isNotNull();
    //Test case 1: Nobody has any friends
    List<User> friendUsers = friendDao.getUserFriends(user1.getUserId());
    assertTrue(friendUsers.isEmpty());
    friendUsers = friendDao.getUserFriends(user2.getUserId());
    assertTrue(friendUsers.isEmpty());
    //Test case 2
    Friend friendship = friendDao.createFriendship(user1, user2);
    friendship.setStatus(2);
    List<Friend> friends = friendDao.findAll();
    assertThat(friends.size()).isEqualTo(1);
    List<User> user1Friends = friendDao.getUserFriends(user1.getUserId());
    assertThat(user1Friends.size()).isEqualTo(1);
    assertThat(user1Friends.get(0).getEmail()).isEqualTo("dan@sun.com");
    List<User> user2Friends = friendDao.getUserFriends(user2.getUserId());
    assertThat(user2Friends.size()).isEqualTo(1);
    assertThat(user2Friends.get(0).getEmail()).isEqualTo("dan@sunnen.me");
  }
}
