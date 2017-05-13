package com.blogggr.dao;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.PostsController;
import com.blogggr.entities.*;
import com.blogggr.entities.Friend_;
import com.blogggr.entities.Post_;
import com.blogggr.entities.User_;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.PageData;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.strategies.validators.GetPostsValidator;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel Sunnen on 20.11.16.
 */
@Repository
public class PostDAOImpl extends GenericDAOImpl<Post> implements PostDAO {

    public enum Visibility {
        onlyGlobal,
        all,
        onlyFriends,
        onlyCurrentUser
    }

    public PostDAOImpl() {
        super(Post.class);
    }

    private final String noResult = "Did not find any posts!";
    private final int friendAccepted = 1;
    private final int defaultLimit = 50;
    private final long defaultMinimumID = -1;

    //Get posts by userID, title and visibility
    @Override
    public PrevNextListPage<Post> getPosts(long userID, Long postUserID, String title, Visibility visibility, Long before, Long after, Integer limit) throws DBException, ResourceNotFoundException {
        try {
            //Check and maybe adjust limit, set default limit
            if (limit == null) limit = Integer.valueOf(defaultLimit);
            else if (limit.intValue() > defaultLimit) limit = Integer.valueOf(defaultLimit);
            //Generate query
            CriteriaQuery<Post> postsQuery = generateQuery(userID, postUserID, title, visibility, before, after, false);
            List<Post> posts = entityManager.createQuery(postsQuery).setMaxResults(limit).getResultList();
            CriteriaQuery<Long> postsCountQuery = generateQuery(userID, postUserID, title, visibility, null, null, true);
            Long totalCount = entityManager.createQuery(postsCountQuery).getSingleResult();
            Collections.sort(posts, (p1,p2)->(int)(p1.getPostID()-p2.getPostID())); //sort such that post id are in ascending order
            Integer numberPageItems = posts.size();
            Long nextAfter = null;
            Long nextBefore = null;
            //Figure out if a post is before or after the posts of this page
            if (totalCount>0 && posts.size()>0){
                CriteriaQuery<Post> beforePostQuery = generateQuery(userID, postUserID, title, visibility, posts.get(0).getPostID(), null, false);
                List<Post> beforePosts = entityManager.createQuery(beforePostQuery).setMaxResults(1).getResultList();
                if (beforePosts.size()==1) nextBefore = posts.get(0).getPostID();

                CriteriaQuery<Post> afterPostQuery = generateQuery(userID, postUserID, title, visibility, null, posts.get(posts.size()-1).getPostID(), false);
                List<Post> afterPosts = entityManager.createQuery(afterPostQuery).setMaxResults(1).getResultList();
                if (afterPosts.size()==1) nextAfter = posts.get(posts.size()-1).getPostID();
            }
            PageData pData = new PageData();
            pData.setPageItemsCount(numberPageItems);
            pData.setTotalCount(totalCount);
            if (nextAfter!=null) pData.setNext(buildNextPageUrl(nextAfter,limit));
            if (nextBefore!=null) pData.setPrevious(buildPreviousPageUrl(nextBefore,limit));
            PrevNextListPage<Post> page = new PrevNextListPage<>(posts,pData);
            return page;
        }
        catch (Exception e) {
            throw new DBException("Database exception!",e);
        }
    }

    private CriteriaQuery generateQuery(long userID, Long postUserID, String title, Visibility visibility, Long before, Long after, boolean countOnly) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query;
        if (!countOnly) query = cb.createQuery(Post.class);
        else query = cb.createQuery(Long.class);
        if (!countOnly) query.distinct(true); //for count: query must be select count (distinct column name), not select distinct column name...
        Root<Post> root = query.from(Post.class);
        //Join from Post over User over Friend over User
        Join<Post, User> postUserJoin = root.join(Post_.user);
        Join<User, Friend> userFriendJoin1 = postUserJoin.join(User_.friends1, JoinType.LEFT);
        Join<Friend, User> friendUserJoin2 = userFriendJoin1.join(Friend_.user2, JoinType.LEFT);
        Join<User, Friend> userFriendJoin2 = postUserJoin.join(User_.friends2, JoinType.LEFT);
        Join<Friend, User> friendUserJoin1 = userFriendJoin2.join(Friend_.user1, JoinType.LEFT);
        //Predicates lists
        List<Predicate> predicatesOr1 = new LinkedList<>();
        List<Predicate> predicatesOr2 = new LinkedList<>();
        List<Predicate> predicatesOr3 = new LinkedList<>();
        List<Predicate> predicatesOr4 = new LinkedList<>();
        //Title condition
        Predicate titleCondition = null;
        if (title != null) {
            title = "%" + title.toLowerCase() + "%"; //substring of title is enough for a match
            titleCondition = cb.like(cb.lower(root.get(Post_.title)), title);
        }
        if (before == null && after == null) {
            after = defaultMinimumID;
        }
        if (before != null && after != null) {
            //Can have either before or after but not both set
            before = null;
        }
        //Post user condition
        Predicate postUserCondition = null;
        if (postUserID != null && visibility!=Visibility.onlyCurrentUser)
            postUserCondition = cb.equal(postUserJoin.get(User_.userID), postUserID.longValue());
        Predicate postAfterCondition = null;
        Predicate postBeforeCondition = null;
        //After postID condition, Before postID condition
        if (!countOnly){
            if (after != null) postAfterCondition = cb.greaterThan(root.get(Post_.postID), after);
            else postBeforeCondition = cb.lessThan(root.get(Post_.postID), before);
        }
        Predicate[] predicatesOr1Array;
        //Visibility global => return all global posts
        if (visibility == Visibility.onlyGlobal) {
            predicatesOr1.add(cb.equal(root.get(Post_.isGlobal), true)); //filter on global posts
            if (titleCondition != null) predicatesOr1.add(titleCondition); //filter on title
            if (postUserCondition != null) predicatesOr1.add(postUserCondition); //filter on userID of poster
            if (!countOnly){
                if (postAfterCondition!=null) predicatesOr1.add(postAfterCondition);
                else predicatesOr1.add(postBeforeCondition);
            }
            predicatesOr1Array = new Predicate[predicatesOr1.size()];
            predicatesOr1.toArray(predicatesOr1Array);
            query.where(
                    cb.and(predicatesOr1Array)
            );
        }
        //Visibility current user
        else if (visibility == Visibility.onlyCurrentUser) { //postUserID is ignored
            predicatesOr1.add(cb.equal(postUserJoin.get(User_.userID), userID)); //filter on current user
            if (titleCondition!=null) predicatesOr1.add(titleCondition); //filter on title
            if (!countOnly){
                if (postAfterCondition!=null) predicatesOr1.add(postAfterCondition);
                else predicatesOr1.add(postBeforeCondition);
            }
            predicatesOr1Array = new Predicate[predicatesOr1.size()];
            predicatesOr1.toArray(predicatesOr1Array);
            query.where(
                    cb.and(predicatesOr1Array)
            );
        }
        //Visibility is friend
        else if (visibility == Visibility.onlyFriends) {
            //Visibility friend => filter friend posts and exclude current user posts
            //AND predicate 1
            predicatesOr1.add(cb.notEqual(postUserJoin.get(User_.userID), userID)); //exclude current user
            predicatesOr1.add(cb.equal(friendUserJoin2.get(User_.userID), userID)); //only friends
            predicatesOr1.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
            predicatesOr1.add(cb.equal(root.get(Post_.isGlobal), false)); //no global posts
            //AND predicate 2
            predicatesOr2.add(cb.notEqual(postUserJoin.get(User_.userID), userID)); //exclude current user
            predicatesOr2.add(cb.equal(friendUserJoin1.get(User_.userID), userID)); //only friends
            predicatesOr2.add(cb.equal(userFriendJoin1.get(Friend_.status), friendAccepted)); //status accepted
            predicatesOr2.add(cb.equal(root.get(Post_.isGlobal), false)); //no global posts
            //Other conditions like the title and the filter on the poster's userID
            if (postUserCondition != null) {
                predicatesOr1.add(postUserCondition);
                predicatesOr2.add(postUserCondition);
            }
            if (titleCondition != null) {
                predicatesOr1.add(titleCondition);
                predicatesOr2.add(titleCondition);
            }
            predicatesOr1Array = new Predicate[predicatesOr1.size()];
            Predicate[] predicatesOr2Array = new Predicate[predicatesOr2.size()];
            predicatesOr1.toArray(predicatesOr1Array);
            predicatesOr2.toArray(predicatesOr2Array);
            Predicate friendsPredicate = cb.or(
                    cb.and(predicatesOr1Array),
                    cb.and(predicatesOr2Array)
            );
            if (countOnly) query.where(friendsPredicate);
            else{
                if (postAfterCondition!=null) {
                    query.where(
                            cb.and(friendsPredicate,postAfterCondition)
                    );
                }
                else{
                    query.where(
                            cb.and(friendsPredicate,postBeforeCondition)
                    );
                }
            }
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
            predicatesOr1Array = new Predicate[predicatesOr1.size()];
            Predicate[] predicatesOr2Array = new Predicate[predicatesOr2.size()];
            Predicate[] predicatesOr3Array = new Predicate[predicatesOr3.size()];
            Predicate[] predicatesOr4Array = new Predicate[predicatesOr4.size()];
            predicatesOr1.toArray(predicatesOr1Array);
            predicatesOr2.toArray(predicatesOr2Array);
            predicatesOr3.toArray(predicatesOr3Array);
            predicatesOr4.toArray(predicatesOr4Array);
            Predicate friendsUserGlobalPredicate = cb.or(
                    cb.and(predicatesOr1Array), //either a friend relationship 1
                    cb.and(predicatesOr2Array), //either a friend relationship 2
                    cb.and(predicatesOr3Array), //either current user
                    cb.and(predicatesOr4Array) //either global post
            );
            if (countOnly) query.where(friendsUserGlobalPredicate);
            else{
                if (postAfterCondition!=null) {
                    query.where(
                            cb.and(friendsUserGlobalPredicate,postAfterCondition)
                    );
                }
                else{
                    query.where(
                            cb.and(friendsUserGlobalPredicate,postBeforeCondition)
                    );
                }
            }
        }
        //Order by post ID and eventually limit
        if (countOnly) query.select(cb.countDistinct(root));
        else{
            if (after!=null) query.orderBy(cb.asc(root.get(Post_.postID)));
            else query.orderBy(cb.desc(root.get(Post_.postID)));
        }
        return query;
    }

    private String buildNextPageUrl(Long next, Integer limit){
        if (next==null || limit==null) throw new IllegalArgumentException("Next and limit should not be null!");
        return AppConfig.fullBaseUrl + PostsController.postsPath + "?" + GetPostsValidator.afterKey
                + "=" + String.valueOf(next) + "&" + GetPostsValidator.limitKey + "=" + limit;
    }

    private String buildPreviousPageUrl(Long previous, Integer limit){
        if (previous==null || limit==null) throw new IllegalArgumentException("Previous and limit should not be null!");
        return AppConfig.fullBaseUrl + PostsController.postsPath + "?" + GetPostsValidator.beforeKey
                + "=" + String.valueOf(previous) + "&" + GetPostsValidator.limitKey + "=" + limit;
    }

    @Override
    public Post getPostByUserAndLabel(Long userID, Long postUserID, String postShortTitle){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);
        Join<Post, User> postUserJoin = root.join(Post_.user, JoinType.LEFT);
        query.where(
                cb.and(
                        cb.equal(root.get(Post_.shortTitle),postShortTitle),
                        cb.equal(postUserJoin.get(User_.userID),postUserID)
                )
        );
        return entityManager.createQuery(query).getSingleResult();
    }
}
