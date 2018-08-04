package com.blogggr.dao;

import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Daniel Sunnen on 27.11.16.
 */
@Repository
public class FriendDao extends GenericDaoImpl<Friend> {

  @Autowired
  private SimpleBundleMessageSource messageSource;

  public FriendDao() {
    super(Friend.class);
  }

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";
  private static final String STATUS = "status";

  public Friend createFriendship(User initUser, User user1, User user2) {
    if (user1.getUserId() == null || user2.getUserId() == null || user1.getUserId()
        .equals(user2.getUserId())) {
      throw new IllegalArgumentException(
          messageSource.getMessage("FriendDao.createFriendship.userNull"));
    }
    //First check if the friendship exists already
    if (getFriendByUserIds(user1.getUserId(), user2.getUserId()) != null) {
      throw new IllegalArgumentException(
          messageSource.getMessage("FriendDao.createFriendship.existAlready"));
    }
    //Create the friendship
    Friend friend = new Friend();
    if (user1.getUserId() < user2.getUserId()) {
      friend.setUser1(user1);
      friend.setUser2(user2);
    } else {
      friend.setUser1(user2);
      friend.setUser2(user1);
    }
    friend.setLastActionTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    friend.setStatus(0); //pending friendship status
    friend.setLastActionUserId(initUser);
    if (user1.getUserId() < user2.getUserId()) {
      user1.getFriends1().add(friend);
      user2.getFriends2().add(friend);
    } else {
      user1.getFriends2().add(friend);
      user2.getFriends1().add(friend);
    }
    save(friend);
    return friend;
  }

  public List<User> getUserFriends(long userId) {
    logger.debug("getUserFriends - userId: {}", userId);
    //Combine users from two queries
    List<User> friends = getUserFriendsHalf(userId, true);
    friends.addAll(getUserFriendsHalf(userId, false));
    return friends;
  }

  private List<User> getUserFriendsHalf(long userId, boolean userOne) {
    logger.debug("getUserFriendsHalf - userId: {}, userOne: {}", userId, userOne);
    /**
     * SQL to produce (userOne boolean selects whether user one or two is selected):
     * SELECT u2.* FROM blogggr.friends f
     * JOIN blogggr.users u1 ON f.useroneid=u1.userid
     * JOIN blogggr.users u2 ON f.usertwoid=u2.userid
     * WHERE f.status=2 AND u1.userID=userID;
     */
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<Friend> root = query.from(Friend.class);
    Join<Friend, User> user1Join = root.join(USER_1);
    Join<Friend, User> user2Join = root.join(USER_2);
    if (!userOne) {
      query.select(user2Join);
      query.where(
          cb.and(
              cb.equal(root.get(STATUS), 1),
              cb.equal(user1Join.get("userId"), userId)
          )
      );
    } else {
      query.select(user1Join);
      query.where(
          cb.and(
              cb.equal(root.get(STATUS), 1),
              cb.equal(user2Join.get("userId"), userId)
          )
      );
    }
    return entityManager.createQuery(query).getResultList();
  }

  private Friend getFriendByUserIdsGeneric(long userId1, long userId2, Integer state) {
    logger.debug("getFriendByUserIDs - userId1: {}, userId2: {}", userId1, userId2);
    if (userId1 == userId2) {
      return null;
    }
    long userSmall;
    long userBig;
    if (userId1 < userId2) {
      userSmall = userId1;
      userBig = userId2;
    } else {
      userSmall = userId2;
      userBig = userId1;
    }
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Friend> query = cb.createQuery(Friend.class);
      Root<Friend> root = query.from(Friend.class);
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get(USER_1), userSmall));
      predicates.add(cb.equal(root.get(USER_2), userBig));
      if (state != null) {
        predicates.add(cb.equal(root.get(STATUS), state));
      }
      query.where(
          cb.and(
            predicates.toArray(new Predicate[predicates.size()])
          )
      );
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public Friend getFriendByUserIds(long userId1, long userId2) {
    logger.debug("getFriendByUserIDs - userId1: {}, userId2: {}", userId1, userId2);
    return getFriendByUserIdsGeneric(userId1, userId2, null);
  }

  public Friend getFriendByUserIdsAndState(long userId1, long userId2, int state) {
    logger
        .debug("getFriendByUserIdsAndState - userId1: {}, userId2: {}, state: {}", userId1, userId2,
            state);
    return getFriendByUserIdsGeneric(userId1, userId2, state);
  }
}
