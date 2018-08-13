package com.blogggr.service;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogggr.dao.FriendDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.FriendDataBase;
import com.blogggr.dto.FriendDataUpdate;
import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.services.FriendService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 04.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FriendServiceTest {

  @MockBean
  private UserDao userDao;

  @MockBean
  private FriendDao friendDao;

  @Autowired
  private FriendService friendService;

  @Test
  public void createFriend_Normal() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    User user1 = new User();
    user1.setUserId(1L);
    user1.setEmail("dan@dan.com");
    when(userDao.findById(1L)).thenReturn(user1);
    User user2 = new User();
    user2.setUserId(2L);
    user2.setEmail("dan2@dan.com");
    when(userDao.findById(2L)).thenReturn(user2);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setStatus(0);
    when(friendDao.createFriendship(user1, user1, user2)).thenReturn(friend);
    Friend dbFriend = friendService.createFriend(1L, friendData);
    assertThat(dbFriend.getStatus()).isEqualTo(0);
    assertThat(dbFriend.getUser1().getEmail()).isEqualTo("dan@dan.com");
    assertThat(dbFriend.getUser2().getEmail()).isEqualTo("dan2@dan.com");
  }

  @Test
  public void createFriend_User_Not_In_Relationship() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    try {
      friendService.createFriend(10L, friendData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Current user must be a part of the new friendship");
    }
  }

  @Test
  public void createFriend_Oneself_Friendship() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(100L);
    friendData.setUserId2(100L);
    try {
      friendService.createFriend(100L, friendData);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Can not be befriended with oneself");
    }
  }

  @Test
  public void createFriend_UserSmall_Not_Found() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    User user1 = new User();
    user1.setUserId(1L);
    user1.setEmail("dan@dan.com");
    when(userDao.findById(1L)).thenReturn(null);
    User user2 = new User();
    user2.setUserId(2L);
    user2.setEmail("dan2@dan.com");
    when(userDao.findById(2L)).thenReturn(user2);
    try {
      friendService.createFriend(1L, friendData);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void createFriend_UserBig_Not_Found() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    User user1 = new User();
    user1.setUserId(1L);
    user1.setEmail("dan@dan.com");
    when(userDao.findById(1L)).thenReturn(user1);
    User user2 = new User();
    user2.setUserId(2L);
    user2.setEmail("dan2@dan.com");
    when(userDao.findById(2L)).thenReturn(null);
    try {
      friendService.createFriend(1L, friendData);
      fail();
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("User not found");
    }
  }

  @Test
  public void createFriend_DataException() {
    FriendDataBase friendData = new FriendDataBase();
    friendData.setUserId1(1L);
    friendData.setUserId2(2L);
    User user1 = new User();
    user1.setUserId(1L);
    user1.setEmail("dan@dan.com");
    when(userDao.findById(1L)).thenReturn(user1);
    User user2 = new User();
    user2.setUserId(2L);
    user2.setEmail("dan2@dan.com");
    when(userDao.findById(2L)).thenReturn(user2);
    doThrow(new InvalidDataAccessApiUsageException("Data Access Exception")).when(friendDao)
        .createFriendship(user1, user1, user2);
    try {
      friendService.createFriend(1L, friendData);
      fail();
    } catch (DataAccessException e) {
      assertThat(e.getMessage()).contains("Data Access Exception");
    }
  }

  @Test
  public void updateFriend_Normal() {
    //User 2 confirms friend request initiated by user 1
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = mock(Friend.class);
    when(friend.getStatus()).thenReturn(0);
    when(friend.getLastActionUserId()).thenReturn(user1);
    when(userDao.findById(2L)).thenReturn(user2);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    friendService.updateFriend(2L, 1L, 2L, friendDataUpdate);
    ArgumentCaptor<Integer> statusArg = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(friend).setStatus(statusArg.capture());
    verify(friend).setLastActionUserId(userArg.capture());
    assertThat(statusArg.getValue()).isEqualTo(1);
    assertThat(userArg.getValue()).isEqualTo(user2);
  }

  @Test
  public void updateFriend_User1_Confirms_His_Own_Friend_Request() {
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setLastActionUserId(user1);
    friend.setStatus(0);
    when(userDao.findById(1L)).thenReturn(user1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    try {
      friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Cannot update friend status");
    }
  }

  @Test
  public void updateFriend_User_Not_In_Friendship() {
    try {
      friendService.updateFriend(10L, 1L, 2L, null);
      fail();
    } catch (NotAuthorizedException e) {
      assertThat(e.getMessage()).contains("Current user must be a part of the new friendship");
    }
  }

  @Test
  public void updateFriend_Friendship_Not_Found() {
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(null);
    try {
      friendService.updateFriend(2L, 1L, 2L, friendDataUpdate);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Friendship not found");
    }
  }

  @Test
  public void updateFriend_User_Not_Found() {
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setLastActionUserId(user1);
    friend.setStatus(0);
    when(userDao.findById(1L)).thenReturn(null);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    try {
      friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage().contains("User not found"));
    }
  }

  @Test
  public void updateFriend_Set_To_Blocked() {
    //User 2 confirms friend request initiated by user 1
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(3);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = mock(Friend.class);
    when(friend.getStatus()).thenReturn(1);
    when(userDao.findById(1L)).thenReturn(user1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
    ArgumentCaptor<Integer> statusArg = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(friend).setStatus(statusArg.capture());
    verify(friend).setLastActionUserId(userArg.capture());
    assertThat(statusArg.getValue()).isEqualTo(3);
    assertThat(userArg.getValue()).isEqualTo(user1);
  }

  @Test
  public void updateFriend_Set_To_Accepted_From_Blocked() {
    //User 2 confirms friend request initiated by user 1
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = mock(Friend.class);
    when(friend.getStatus()).thenReturn(3);
    when(friend.getLastActionUserId()).thenReturn(user1);
    when(userDao.findById(1L)).thenReturn(user1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
    ArgumentCaptor<Integer> statusArg = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(friend).setStatus(statusArg.capture());
    verify(friend).setLastActionUserId(userArg.capture());
    assertThat(statusArg.getValue()).isEqualTo(1);
    assertThat(userArg.getValue()).isEqualTo(user1);
  }

  @Test
  public void updateFriend_Set_To_Accepted_From_Declined() {
    //User 2 confirms friend request initiated by user 1
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(1);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = mock(Friend.class);
    when(friend.getStatus()).thenReturn(2);
    when(friend.getLastActionUserId()).thenReturn(user1);
    when(userDao.findById(1L)).thenReturn(user1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    when(friendDao.update(any(Friend.class))).thenReturn(null);
    friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
    ArgumentCaptor<Integer> statusArg = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(friend).setStatus(statusArg.capture());
    verify(friend).setLastActionUserId(userArg.capture());
    assertThat(statusArg.getValue()).isEqualTo(1);
    assertThat(userArg.getValue()).isEqualTo(user1);
  }

  @Test
  public void updateFriend_Set_To_Nothing_From_Accepted() {
    FriendDataUpdate friendDataUpdate = new FriendDataUpdate();
    friendDataUpdate.setAction(0);
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = mock(Friend.class);
    when(friend.getStatus()).thenReturn(2);
    when(friend.getLastActionUserId()).thenReturn(user1);
    when(userDao.findById(1L)).thenReturn(user1);
    when(friendDao.getFriendByUserIds(1L, 2L)).thenReturn(friend);
    try {
      friendService.updateFriend(1L, 1L, 2L, friendDataUpdate);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Cannot update friend status");
    }
  }

  @Test
  public void deleteFriend_Normal(){
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    when(friendDao.findByIds(any(Long.class),any(Long.class))).thenReturn(friend);
    doNothing().when(friendDao).delete(any(Friend.class));
    friendService.deleteFriend(1L,2L, 1L);
  }

  @Test
  public void deleteFriend_Friend_Null(){
    when(friendDao.findById(any(Long.class))).thenReturn(null);
    doNothing().when(friendDao).delete(any(Friend.class));
    try {
      friendService.deleteFriend(1L, 2L, 1L);
      fail();
    }catch(ResourceNotFoundException e){
      assertThat(e.getMessage()).contains("Friendship not found");
    }
  }

  @Test
  public void deleteFriend_User_Not_In_Friendship(){
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    when(friendDao.findByIds(any(Long.class),any(Long.class))).thenReturn(friend);
    doNothing().when(friendDao).delete(any(Friend.class));
    try {
      friendService.deleteFriend(1L, 2L, 10L);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not authorized to delete friendship");
    }
  }

  @Test
  public void getFriend_Normal(){
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setStatus(1);
    when(friendDao.findByIds(any(Long.class),any(Long.class))).thenReturn(friend);
    Friend dbFriend = friendService.getFriend(1L, 2L, 1L);
    assertThat(dbFriend.getStatus()).isEqualTo(1);
  }

  @Test
  public void getFriend_Friend_Null(){
    when(friendDao.findByIds(any(Long.class),any(Long.class))).thenReturn(null);
    try {
      friendService.getFriend(1L, 1L, 1L);
      fail();
    }catch(ResourceNotFoundException e) {
      assertThat(e.getMessage()).contains("Friendship not found");
    }
  }

  @Test
  public void getFriend_User_Not_In_Friendship(){
    User user1 = new User();
    user1.setUserId(1L);
    User user2 = new User();
    user2.setUserId(2L);
    Friend friend = new Friend();
    friend.setUser1(user1);
    friend.setUser2(user2);
    friend.setStatus(1);
    when(friendDao.findByIds(any(Long.class),any(Long.class))).thenReturn(friend);
    try {
      friendService.getFriend(1L, 2L, 10L);
      fail();
    }catch(NotAuthorizedException e){
      assertThat(e.getMessage()).contains("Not allowed to view this friendship");
    }
  }

  @Test
  public void getFriends_Normal(){
    User user = new User();
    user.setEmail("dan@dan.com");
    List<User> users = new ArrayList<>();
    users.add(user);
    when(friendDao.getUserFriends(1L)).thenReturn(users);
    List<User> dbUsers = friendService.getFriends(1L);
    assertThat(dbUsers.size()).isEqualTo(1);
    assertThat(dbUsers.get(0).getEmail()).isEqualTo("dan@dan.com");
  }
}
