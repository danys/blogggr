package com.blogggr.dao;

import com.blogggr.entities.Friend;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Daniel Sunnen on 27.11.16.
 */
@Repository
public class FriendDAOImpl extends GenericDAOImpl<Friend> implements FriendDAO{

    private final String noResult = "Did not find any friends!";

    public FriendDAOImpl(){
        super(Friend.class);
    }

    public List<Friend> getUserFriends(long userID){
        //select * from blogggr.friends f join blogggr.users u1 on f.useroneid=u1.userid join blogggr.users u2 on f.usertwoid=u2.userid where f.status=2;
        /*try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Friend> query = cb.createQuery(Friend.class);
            Root<Friend> friend = query.from(Friend.class);
            Join<Friend> user1 = friend.join("user1");
            query.where(cb.equal(root.get("sessionHash"), sessionHash));
            return entityManager.createQuery(query).getSingleResult();
        }
        catch(NoResultException e){
            throw new ResourceNotFoundException(noResult);
        }
        catch(Exception e){
            throw new DBException("Database exception!");
        }*/
        //TODO
        return null;
    }
}
