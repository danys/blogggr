package com.blogggr.services;

import com.blogggr.dao.FriendDao;
import com.blogggr.dao.UserDao;
import com.blogggr.dto.FriendDataBase;
import com.blogggr.dto.FriendDataUpdate;
import com.blogggr.entities.Friend;
import com.blogggr.entities.FriendPk;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.utilities.SimpleBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
@Service
@Transactional
public class FriendService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private FriendDao friendDao;

  @Autowired
  private SimpleBundleMessageSource messageSource;

  private static final String USER_NOT_FOUND = "FriendService.userNotFoundException";
  private static final String FRIEND_NOT_FOUND = "FriendService.friendNotFoundException";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public Friend createFriend(long userId, FriendDataBase friendData) {
    logger.debug("FriendService | createFriend - userId: {}, friendData: {}", userId, friendData);
    long userID1 = friendData.getUserId1();
    long userID2 = friendData.getUserId2();
    if (userID1 != userId && userID2 != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.createFriend.currentUserNotPresentException"));
    } else if (userID1 == userID2) {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.createFriend.oneSelfFriendException"));
    }
    long small;
    long big;
    small = (userID1 < userID2) ? userID1 : userID2;
    big = (userID1 > userID2) ? userID1 : userID2;
    //Fetch users
    User userSmall = userDao.findById(small);
    if (userSmall == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(USER_NOT_FOUND));
    }
    User userBig = userDao.findById(big);
    if (userBig == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(USER_NOT_FOUND));
    }
    return friendDao.createFriendship(userSmall, userBig);
  }

  public void updateFriend(long userId, long user1, long user2, FriendDataUpdate friendData) {
    logger.debug("FriendService | updateFriend - userId: {}, user1: {}, user2: {}", userId, user1, user2);
    if (user1 != userId && user2 != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.createFriend.currentUserNotPresentException"));
    }
    long small;
    long big;
    small = (user1 < user2) ? user1 : user2;
    big = (user1 > user2) ? user1 : user2;
    Friend friend = friendDao.getFriendByUserIds(small, big);
    if (friend == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(FRIEND_NOT_FOUND));
    }
    if (friend.getUser1().getUserId() != userId && friend.getUser2().getUserId() != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.updateFriend.notAuthorizedException"));
    }
    User currentUser = userDao.findById(userId);
    if (currentUser == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(USER_NOT_FOUND));
    }
    if (friend.getStatus() == 0 && friendData.getAction() != 0) { //accept friend request
      if (friend.getLastActionUserId().getUserId() != userId) {
        //user at one side sets status to pending the other user can set it to 1, 2 or 3
        friend.setStatus(friendData.getAction());
        friend.setLastActionUserId(currentUser);
        friendDao.update(friend);
      } else {
        throw new NotAuthorizedException(messageSource.getMessage("FriendService.updateFriend.unableUpdateException"));
      }
    } else if (friend.getStatus() == 1 && friendData.getAction() == 3) { //set status to blocked
      friend.setStatus(friendData.getAction());
      friend.setLastActionUserId(currentUser);
      friendDao.update(friend);
    } else if ((friend.getStatus() == 2 || friend.getStatus() == 3)
        && friendData.getAction() == 1) { //set status to accepted from declined or blocked
      if (friend.getLastActionUserId().getUserId() == userId) {
        friend.setStatus(friendData.getAction());
        friend.setLastActionUserId(currentUser);
        friendDao.update(friend);
      }
    } else {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.updateFriend.unableUpdateException"));
    }
  }

  public void deleteFriend(long friendId, long userId) {
    logger.debug("FriendService | deleteFriend - friendId: {}, userId: {}", friendId, userId);
    Friend friend = friendDao.findById(friendId);
    if (friend == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(FRIEND_NOT_FOUND));
    }
    if (friend.getUser1().getUserId() != userId || friend.getUser2().getUserId() != userId) {
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.deleteFriend.notAuthorizedException"));
    }
    friendDao.delete(friend);
  }

  public Friend getFriend(long friendId, long userId) {
    logger.debug("FriendService | getFriend - friendId: {}, userId: {}", friendId, userId);
    Friend friend = friendDao.findById(friendId);
    if (friend == null) {
      throw new ResourceNotFoundException(messageSource.getMessage(FRIEND_NOT_FOUND));
    } else if (friend.getUser1().getUserId() != userId || friend.getUser2().getUserId() != userId){
      throw new NotAuthorizedException(messageSource.getMessage("FriendService.getFriend.notAuthorizedException"));
    }
    return friend;
  }

  public List<User> getFriends(long userId) {
    logger.debug("FriendService | getFriends - userId: {}", userId);
    return friendDao.getUserFriends(userId);
  }
}
