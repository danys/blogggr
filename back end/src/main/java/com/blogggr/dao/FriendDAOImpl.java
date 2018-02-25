package com.blogggr.dao;

import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class FriendDAOImpl extends GenericDAOImpl<Friend> implements FriendDAO {

  private final String noResult = "Did not find any friends!";

  public FriendDAOImpl() {
    super(Friend.class);
  }

  private final Log logger = LogFactory.getLog(this.getClass());

  @Override
  public List<User> getUserFriends(long userID) throws ResourceNotFoundException, DBException {
    //Combine users from two queries
    List<User> friends = getUserFriendsHalf(userID, true);
    friends.addAll(getUserFriendsHalf(userID, false));
    return friends;
  }

  private List<User> getUserFriendsHalf(long userID, boolean userOne)
      throws ResourceNotFoundException, DBException {
    /**
     * SQL to produce (userOne boolean selects whether user one or two is selected):
     * SELECT u2.* FROM blogggr.friends f
     * JOIN blogggr.users u1 ON f.useroneid=u1.userid
     * JOIN blogggr.users u2 ON f.usertwoid=u2.userid
     * WHERE f.status=2 AND u1.userID=userID;
     */
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> query = cb.createQuery(User.class);
      Root<Friend> root = query.from(Friend.class);
      Join<Friend, User> user1Join = root.join("user1");
      Join<Friend, User> user2Join = root.join("user2");
      if (!userOne) {
        query.select(user2Join);
        query.where(
            cb.and(
                cb.equal(root.get("status"), 1),
                cb.equal(user1Join.get("userId"), userID)
            )
        );
      } else {
        query.select(user1Join);
        query.where(
            cb.and(
                cb.equal(root.get("status"), 1),
                cb.equal(user2Join.get("userId"), userID)
            )
        );
      }
      return entityManager.createQuery(query).getResultList();
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(noResult);
    } catch (Exception e) {
      throw new DBException("Database exception!");
    }
  }

  @Override
  public Friend getFriendByUserIDs(long userID1, long userID2)
      throws ResourceNotFoundException, DBException {
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Friend> query = cb.createQuery(Friend.class);
      Root<Friend> root = query.from(Friend.class);
      query.where(
          cb.and(
              cb.equal(root.get("user1"), userID1),
              cb.equal(root.get("user2"), userID2)
          )
      );
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(noResult);
    } catch (Exception e) {
      throw new DBException("Database exception!");
    }
  }

  @Override
  public Friend getFriendByUserIDsAndState(long userID1, long userID2, int state)
      throws ResourceNotFoundException, DBException {
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Friend> query = cb.createQuery(Friend.class);
      Root<Friend> root = query.from(Friend.class);
      query.where(
          cb.and(
              cb.equal(root.get("user1"), userID1),
              cb.equal(root.get("user2"), userID2),
              cb.equal(root.get("status"), state)
          )
      );
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(noResult);
    } catch (Exception e) {
      throw new DBException("Database exception!");
    }
  }
}
