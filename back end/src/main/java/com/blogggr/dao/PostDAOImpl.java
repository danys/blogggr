package com.blogggr.dao;

import com.blogggr.entities.*;
import com.blogggr.entities.Friend_;
import com.blogggr.entities.Post_;
import com.blogggr.entities.User_;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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
    private final int friendAccepted = 1;
    private final int defaultLimit = 50;

    //Get posts by userID, title and visibility
    @Override
    public List<Post> getPosts(long userID, Long postUserID, String title, Visibility visibility, Long before, Long after, Integer limit) throws DBException, ResourceNotFoundException{
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Post> query = cb.createQuery(Post.class);
            query.distinct(true);
            Root<Post> root = query.from(Post.class);
            //Join from Post over User over Friend over User
            Join<Post, User> postUserJoin = root.join(Post_.user);
            Join<User, Friend> userFriendJoin1 = postUserJoin.join(User_.friends1,JoinType.LEFT);
            Join<Friend, User> friendUserJoin2 = userFriendJoin1.join(Friend_.user2,JoinType.LEFT);
            Join<User, Friend> userFriendJoin2 = postUserJoin.join(User_.friends2,JoinType.LEFT);
            Join<Friend, User> friendUserJoin1 = userFriendJoin2.join(Friend_.user1,JoinType.LEFT);
            //Predicates lists
            List<Predicate> predicatesOr1 = new LinkedList<>();
            List<Predicate> predicatesOr2 = new LinkedList<>();
            List<Predicate> predicatesOr3 = new LinkedList<>();
            List<Predicate> predicatesOr4 = new LinkedList<>();
            //Title condition
            Predicate titleCondition = null;
            if (title != null) {
                title = "%"+title.toLowerCase()+"%"; //substring of title is enough for a match
                titleCondition = cb.like(cb.lower(root.get(Post_.title)), title);
            }
            //Post user condition
            Predicate postUserCondition = null;
            if (postUserID != null)
                postUserCondition = cb.equal(postUserJoin.get(User_.userID), postUserID.longValue());
            //After postID condition
            Predicate postAfterCondition = null;
            if (after != null)
                postAfterCondition = cb.greaterThan(root.get(Post_.postID), after);
            //Before postID condition
            Predicate postBeforeCondition = null;
            if (before != null)
                postBeforeCondition = cb.lessThan(root.get(Post_.postID), before);
            //Check and maybe adjust limit, set default limit
            if (limit==null) limit = Integer.valueOf(defaultLimit);
            else if (limit.intValue()>defaultLimit) limit = Integer.valueOf(defaultLimit);
            //Visibility global => return all global posts
            if (visibility == Visibility.onlyGlobal) {
                predicatesOr1.add(cb.equal(root.get(Post_.isGlobal), true)); //filter on global posts
                if (titleCondition!=null) predicatesOr1.add(titleCondition); //filter on title
                if (postUserCondition!=null) predicatesOr1.add(postUserCondition); //filter on userID of poster
                if (postAfterCondition!=null) predicatesOr1.add(postAfterCondition);
                if (postBeforeCondition!=null) predicatesOr1.add(postBeforeCondition);
                Predicate[] predicatesOr1Array = new Predicate[predicatesOr1.size()];
                predicatesOr1.toArray(predicatesOr1Array);
                query.where(
                        cb.and(predicatesOr1Array)
                );
            }
            //Visibility current user
            else if (visibility == Visibility.onlyCurrentUser) {
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
                //OR predicate 3
                predicatesOr3.add(cb.equal(postUserJoin.get(User_.userID), userID)); //either current user
                //OR predicate 4
                predicatesOr4.add(cb.equal(root.get(Post_.isGlobal), true)); //either global post
                //Other conditions like the title and the filter on the poster's userID
                if (postUserCondition != null) {
                    predicatesOr1.add(postUserCondition);
                    predicatesOr2.add(postUserCondition);
                    predicatesOr3.add(postUserCondition);
                    predicatesOr4.add(postUserCondition);
                }
                if (titleCondition != null) {
                    predicatesOr1.add(titleCondition);
                    predicatesOr2.add(titleCondition);
                    predicatesOr3.add(titleCondition);
                    predicatesOr4.add(titleCondition);
                }
                if (postAfterCondition!=null){
                    predicatesOr1.add(postAfterCondition);
                    predicatesOr2.add(postAfterCondition);
                    predicatesOr3.add(postAfterCondition);
                    predicatesOr4.add(postAfterCondition);
                }
                if (postBeforeCondition!=null){
                    predicatesOr1.add(postBeforeCondition);
                    predicatesOr2.add(postBeforeCondition);
                    predicatesOr3.add(postBeforeCondition);
                    predicatesOr4.add(postBeforeCondition);
                }
                Predicate[] predicatesOr1Array = new Predicate[predicatesOr1.size()];
                Predicate[] predicatesOr2Array = new Predicate[predicatesOr2.size()];
                Predicate[] predicatesOr3Array = new Predicate[predicatesOr3.size()];
                Predicate[] predicatesOr4Array = new Predicate[predicatesOr4.size()];
                predicatesOr1.toArray(predicatesOr1Array);
                predicatesOr2.toArray(predicatesOr2Array);
                predicatesOr3.toArray(predicatesOr3Array);
                predicatesOr4.toArray(predicatesOr4Array);
                query.where(
                        cb.or(
                                cb.and(predicatesOr1Array), //either a friend relationship 1
                                cb.and(predicatesOr2Array), //either a friend relationship 2
                                cb.and(predicatesOr3Array), //either current user
                                cb.and(predicatesOr4Array) //either global post
                        )
                );
            }
            //Order by post ID and eventually limit
            query.orderBy(cb.asc(root.get(Post_.postID)));
            return entityManager.createQuery(query).setMaxResults(limit).getResultList();
        }
        catch(NoResultException e){
            throw new ResourceNotFoundException(noResult);
        }
        catch(Exception e) {
            throw new DBException("Database exception!");
        }
    }
}
