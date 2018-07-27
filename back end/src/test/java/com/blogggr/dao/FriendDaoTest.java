package com.blogggr.dao;

import static com.blogggr.dao.UserDaoTest.createUser;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
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

  @Test
  @Transactional
  public void createFriendship_Normal(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    Friend friendship = friendDao.createFriendship(user1, user2);
    assertThat(friendship.getUser1()).isNotNull();
    assertThat(friendship.getUser1().getUserId()).isEqualTo(user1.getUserId());
    assertThat(friendship.getUser2()).isNotNull();
    assertThat(friendship.getUser2().getUserId()).isEqualTo(user2.getUserId());
    //Check user 1
    List<Friend> leftFriendsU1 = user1.getFriends1();
    List<Friend> rightFriendsU1 = user1.getFriends2();
    assertThat(leftFriendsU1.size()).isEqualTo(1);
    assertThat(leftFriendsU1.get(0).getUser1().getEmail()).isEqualTo("dan@sunnen.me");
    assertThat(leftFriendsU1.get(0).getUser2().getEmail()).isEqualTo("dan@sun.com");
    assertThat(rightFriendsU1.size()).isEqualTo(0);
    //Check user 2
    List<Friend> leftFriendsU2 = user2.getFriends1();
    List<Friend> rightFriendsU2 = user2.getFriends2();
    assertThat(leftFriendsU2.size()).isEqualTo(0);
    assertThat(rightFriendsU2.size()).isEqualTo(1);
    assertThat(rightFriendsU2.get(0).getUser1().getEmail()).isEqualTo("dan@sunnen.me");
    assertThat(rightFriendsU2.get(0).getUser2().getEmail()).isEqualTo("dan@sun.com");
    //Add a third user
    User user3 = createUser("John", "Smith", "john@smith.com", 1);
    userDao.save(user3);
    Friend friendship13 = friendDao.createFriendship(user1, user3);
    assertThat(friendship13.getUser1()).isNotNull();
    assertThat(friendship13.getUser1().getUserId()).isEqualTo(user1.getUserId());
    assertThat(friendship13.getUser2()).isNotNull();
    assertThat(friendship13.getUser2().getUserId()).isEqualTo(user3.getUserId());
    assertThat(leftFriendsU1.size()).isEqualTo(2);
    assertThat(rightFriendsU1.size()).isEqualTo(0);
    assertThat(leftFriendsU2.size()).isEqualTo(0);
    assertThat(rightFriendsU2.size()).isEqualTo(1);
    List<Friend> leftFriendsU3 = user3.getFriends1();
    List<Friend> rightFriendsU3 = user3.getFriends2();
    assertThat(leftFriendsU3.size()).isEqualTo(0);
    assertThat(rightFriendsU3.size()).isEqualTo(1);
    assertThat(rightFriendsU3.get(0).getUser1().getEmail()).isEqualTo("dan@sunnen.me");
  }

  @Test
  @Transactional
  public void createFriendship_Normal_Users_Inverted(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    userDao.save(user1);
    Friend friendship = friendDao.createFriendship(user1, user2);
    assertThat(friendship.getUser1()).isNotNull();
    assertThat(friendship.getUser1().getUserId()).isEqualTo(user2.getUserId());
    assertThat(friendship.getUser2()).isNotNull();
    assertThat(friendship.getUser2().getUserId()).isEqualTo(user1.getUserId());
    //Check user 1
    List<Friend> leftFriendsU1 = user1.getFriends1();
    List<Friend> rightFriendsU1 = user1.getFriends2();
    assertThat(leftFriendsU1.size()).isEqualTo(0);
    assertThat(rightFriendsU1.size()).isEqualTo(1);
    assertThat(rightFriendsU1.get(0).getUser1().getEmail()).isEqualTo("dan@sun.com");
    assertThat(rightFriendsU1.get(0).getUser2().getEmail()).isEqualTo("dan@sunnen.me");
    //Check user 2
    List<Friend> leftFriendsU2 = user2.getFriends1();
    List<Friend> rightFriendsU2 = user2.getFriends2();
    assertThat(leftFriendsU2.size()).isEqualTo(1);
    assertThat(leftFriendsU2.get(0).getUser1().getEmail()).isEqualTo("dan@sun.com");
    assertThat(leftFriendsU2.get(0).getUser2().getEmail()).isEqualTo("dan@sunnen.me");
    assertThat(rightFriendsU2.size()).isEqualTo(0);
    //Add a third user
    User user3 = createUser("John", "Smith", "john@smith.com", 1);
    userDao.save(user3);
    Friend friendship13 = friendDao.createFriendship(user1, user3);
    assertThat(friendship13.getUser1()).isNotNull();
    assertThat(friendship13.getUser1().getUserId()).isEqualTo(user1.getUserId());
    assertThat(friendship13.getUser2()).isNotNull();
    assertThat(friendship13.getUser2().getUserId()).isEqualTo(user3.getUserId());
    assertThat(leftFriendsU1.size()).isEqualTo(1);
    assertThat(rightFriendsU1.size()).isEqualTo(1);
    assertThat(leftFriendsU2.size()).isEqualTo(1);
    assertThat(rightFriendsU2.size()).isEqualTo(0);
    List<Friend> leftFriendsU3 = user3.getFriends1();
    List<Friend> rightFriendsU3 = user3.getFriends2();
    assertThat(leftFriendsU3.size()).isEqualTo(0);
    assertThat(rightFriendsU3.size()).isEqualTo(1);
    assertThat(rightFriendsU3.get(0).getUser1().getEmail()).isEqualTo("dan@sunnen.me");
  }

  @Test
  @Transactional
  public void createFriendship_Duplicate_Friendship(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    Friend friendship = friendDao.createFriendship(user1, user2);
    //Try if inserting a duplicate friendship works
    try {
      friendDao.createFriendship(user1, user2);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getCause().getMessage()).contains("exists already");
    }
    //Try it if users are inverted
    try {
      friendDao.createFriendship(user2, user1);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getCause().getMessage()).contains("exists already");
    }
  }

  @Test
  @Transactional
  public void createFriendship_Null(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    Friend friendship;
    //Two users null id
    try {
      friendDao.createFriendship(user1, user2);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getCause().getMessage()).contains("Persisted user must be provided");
    }
    //One user null id
    user1.setUserId(1L);
    try {
      friendDao.createFriendship(user1, user2);
      fail();
    }catch(DataAccessException e){
      Throwable t = e.getCause();
      assertThat(e.getCause().getMessage()).contains("Persisted user must be provided");
    }
    //Other user null id
    user1.setUserId(null);
    user2.setUserId(1L);
    try {
      friendDao.createFriendship(user1, user2);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getCause().getMessage()).contains("Persisted user must be provided");
    }
  }

  @Test
  @Transactional
  public void createFriendship_SameId(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    Friend friendship;
    user1.setUserId(1L);
    user2.setUserId(1L);
    try {
      friendDao.createFriendship(user1, user2);
      fail();
    }catch(DataAccessException e){
      assertThat(e.getCause().getMessage()).contains("Persisted user must be provided");
    }
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

  @Test
  @Transactional
  public void getFriendByUserIds_Normal(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    friendDao.createFriendship(user1, user2);
    Friend friendship = friendDao.getFriendByUserIds(user1.getUserId(), user2.getUserId());
    assertThat(friendship).isNotNull();
    Friend friendship2 = friendDao.getFriendByUserIds(user2.getUserId(), user1.getUserId());
    assertThat(friendship2).isNotNull();
  }

  @Test
  @Transactional
  public void getFriendByUserIds_No_Friendship(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    Friend friendship = friendDao.getFriendByUserIds(user1.getUserId(), user2.getUserId());
    assertThat(friendship).isNull();
  }

  @Test
  @Transactional
  public void getFriendByUserIds_Id_Same(){
    assertThat(friendDao.getFriendByUserIds(1L, 1L)).isNull();
  }

  @Test
  @Transactional
  public void getFriendByUserIdsAndState_Normal(){
    User user1 = createUser("Daniel", "Sunnen", "dan@sunnen.me", 1);
    userDao.save(user1);
    User user2 = createUser("Dan", "Sun", "dan@sun.com", 1);
    userDao.save(user2);
    Friend friend = friendDao.createFriendship(user1, user2);
    friend.setStatus(2);
    assertThat(friendDao.getFriendByUserIdsAndState(user1.getUserId(), user2.getUserId(), 2)).isNotNull();
    assertThat(friendDao.getFriendByUserIdsAndState(user1.getUserId(), user2.getUserId(), 0)).isNull();
    friend.setStatus(0);
    assertThat(friendDao.getFriendByUserIdsAndState(user1.getUserId(), user2.getUserId(), 0)).isNotNull();
  }
}
