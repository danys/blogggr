package com.blogggr.dao;

import com.blogggr.entities.*;
import com.blogggr.entities.Friend_;
import com.blogggr.entities.Post_;
import com.blogggr.entities.User_;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel Sunnen on 20.11.16.
 */
@Repository
public class PostDAOImpl extends GenericDAOImpl<Post> implements PostDAO {

    public enum Visibility{
        onlyGlobal,
        all,
        onlyFriends,
        onlyCurrentUser
    }

    public PostDAOImpl(){
        super(Post.class);
    }

    private final String noResult = "Did not find any posts!";
    private final int friendAccepted = 2;

    //Get posts by userID, title and visibility
    // Visibility = 0 (default) => global + friends + current user
    // Visibility = 1 => only friends
    // Visibility = 2 => only global
    // Visibility = 3 => only current user
    @Override
    public List<Post> getPosts(long userID, Long postUserID, String title, Visibility visibility) throws DBException, ResourceNotFoundException{
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Post> query = cb.createQuery(Post.class);
            Root<Post> root = query.from(Post.class);
            //Join from Post over User over Friend over User
            Join<Post, User> postUserJoin = root.join(Post_.user);
            Join<User, Friend> userFriendJoin1 = postUserJoin.join(User_.friends1);
            Join<Friend, User> friendUserJoin2 = userFriendJoin1.join(Friend_.user2);
            Join<User, Friend> userFriendJoin2 = postUserJoin.join(User_.friends2);
            Join<Friend, User> friendUserJoin1 = userFriendJoin2.join(Friend_.user1);
            //Predicates lists
            List<Predicate> predicatesOr1 = new LinkedList<>();
            List<Predicate> predicatesOr2 = new LinkedList<>();
            Predicate titleCondition = null;
            if (title != null) titleCondition = cb.like(root.get(Post_.title), title);
            Predicate postUserCondition = null;
            if (postUserID != null)
                postUserCondition = cb.equal(postUserJoin.get(User_.userID), postUserID.longValue());
            //Visibility global => return all global posts
            if (visibility == Visibility.onlyGlobal) {
                query.where(
                        cb.and(
                                cb.equal(root.get(Post_.isGlobal), true), //filter on global posts
                                titleCondition, //filter on title
                                postUserCondition //filter on userID of poster

                        )
                );
                return entityManager.createQuery(query).getResultList();
            }
            //Visibility current user
            if (visibility == Visibility.onlyCurrentUser) {
                query.where(
                        cb.and(
                                cb.equal(postUserJoin.get(User_.userID), userID), //filter on current user
                                titleCondition //filter on title
                        )
                );
            }
            //Visibility is friend
            else if (visibility == Visibility.onlyFriends) {
                //Visibility friend => filter friend posts and exclude current user posts
                //OR predicate 1
                predicatesOr1.add(cb.notEqual(postUserJoin.get(User_.userID), userID)); //exclude current user
                predicatesOr1.add(cb.equal(friendUserJoin2.get(User_.userID), userID)); //only friends
                predicatesOr1.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
                predicatesOr1.add(cb.equal(root.get(Post_.isGlobal), false)); //no global posts
                //OR predicate 2
                predicatesOr2.add(cb.notEqual(postUserJoin.get(User_.userID), userID)); //exclude current user
                predicatesOr2.add(cb.equal(friendUserJoin1.get(User_.userID), userID)); //only friends
                predicatesOr2.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
                predicatesOr1.add(cb.equal(root.get(Post_.isGlobal), false)); //no global posts
                //Other conditions like the title and the filter on the poster's userID
                if (postUserCondition != null) {
                    predicatesOr1.add(postUserCondition);
                    predicatesOr2.add(postUserCondition);
                }
                if (titleCondition != null) {
                    predicatesOr1.add(titleCondition);
                    predicatesOr2.add(titleCondition);
                }
                Predicate[] predicatesOr1Array = new Predicate[predicatesOr1.size()];
                Predicate[] predicatesOr2Array = new Predicate[predicatesOr2.size()];
                predicatesOr1.toArray(predicatesOr1Array);
                predicatesOr2.toArray(predicatesOr2Array);
                query.where(
                        cb.or(
                                cb.and(predicatesOr1Array),
                                cb.and(predicatesOr2Array)
                        )
                );
            }
            //Visibility all: friends + global + this user
            else if (visibility == Visibility.all) {
                //Visibility friend => filter friend posts and exclude current user posts
                //OR predicate 1
                predicatesOr1.add(cb.equal(friendUserJoin2.get(User_.userID), userID)); //only friends
                predicatesOr1.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
                //OR predicate 2
                predicatesOr2.add(cb.equal(friendUserJoin1.get(User_.userID), userID)); //only friends
                predicatesOr2.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
                //Other conditions like the title and the filter on the poster's userID
                if (postUserCondition != null) {
                    predicatesOr1.add(postUserCondition);
                    predicatesOr2.add(postUserCondition);
                }
                if (titleCondition != null) {
                    predicatesOr1.add(titleCondition);
                    predicatesOr2.add(titleCondition);
                }
                Predicate[] predicatesOr1Array = new Predicate[predicatesOr1.size()];
                Predicate[] predicatesOr2Array = new Predicate[predicatesOr2.size()];
                predicatesOr1.toArray(predicatesOr1Array);
                predicatesOr2.toArray(predicatesOr2Array);
                query.where(
                        cb.or(
                                cb.and(predicatesOr1Array), //either a friend relationship 1
                                cb.and(predicatesOr2Array), //either a friend relationship 2
                                cb.and(cb.equal(postUserJoin.get(User_.userID), userID)),  //either current user
                                cb.and(cb.equal(root.get(Post_.isGlobal), true)) //either global post
                        )
                );
            }
            return entityManager.createQuery(query).getResultList();
        }
        catch(NoResultException e){
            throw new ResourceNotFoundException(noResult);
        }
        catch(Exception e) {
            throw new DBException("Database exception!");
        }
    }
}
