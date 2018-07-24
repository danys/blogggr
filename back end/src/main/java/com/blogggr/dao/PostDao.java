package com.blogggr.dao;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.PostsController;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.*;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.StringUtilities;
import javax.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by Daniel Sunnen on 20.11.16.
 */
@Repository
public class PostDao extends GenericDaoImpl<Post> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource messageSource;

  public enum Visibility {
    ONLY_GLOBAL,
    ALL,
    ONLY_FRIENDS,
    ONLY_CURRENT_USER
  }

  public static final String POSTER_USER_ID_KEY = "posterUserId";
  public static final String TITLE_KEY = "title";
  public static final String VISIBILITY_KEY = "visibility";
  public static final String MAX_RECORDS_COUNT_KEY = "maxRecordsCount";
  public static final String BEFORE_KEY = "before";
  public static final String AFTER_KEY = "after";

  private static final int FRIEND_ACCEPTED = 1;
  private static final int DEFAULT_LIMIT = 50;
  private static final String USER_ID = "userId";
  private static final String POST_ID = "postId";
  private static final String STATUS = "status";
  private static final String IS_GLOBAL = "isGlobal";

  public PostDao() {
    super(Post.class);
  }

  //Get posts by userId, title and visibility
  public PrevNextListPage<Post> getPosts(PostSearchData postSearchData, User user) {
    logger.debug("PostDao | getPosts - postSearchData: {}, user: {}", postSearchData, user);
    //Check and maybe adjust limit, set default limit
    if (postSearchData.getMaxRecordsCount() == null
        || postSearchData.getMaxRecordsCount().intValue() > DEFAULT_LIMIT) {
      postSearchData.setMaxRecordsCount(DEFAULT_LIMIT);
    }
    //Generate query
    CriteriaQuery<Post> postsQuery = generateQuery(user.getUserId(),
        postSearchData.getPosterUserId(), postSearchData.getTitle(),
        postSearchData.getVisibility(), postSearchData.getBefore(),
        postSearchData.getAfter(), false);
    List<Post> posts = entityManager.createQuery(postsQuery)
        .setMaxResults(postSearchData.getMaxRecordsCount()).getResultList();
    CriteriaQuery<Long> postsCountQuery = generateQuery(user.getUserId(),
        postSearchData.getPosterUserId(), postSearchData.getTitle(),
        postSearchData.getVisibility(),
        null, null, true);
    Long totalCount = entityManager.createQuery(postsCountQuery).getSingleResult();
    Integer numberPageItems = posts.size();
    Long nextAfter = null;
    Long nextBefore = null;
    //Figure out if a post is before or after the posts of this page
    if (totalCount > 0 && !posts.isEmpty()) {
      CriteriaQuery<Post> beforePostQuery = generateQuery(user.getUserId(),
          postSearchData.getPosterUserId(), postSearchData.getTitle(),
          postSearchData.getVisibility(),
          null, posts.get(0).getPostId(), false);
      List<Post> beforePosts = entityManager.createQuery(beforePostQuery).setMaxResults(1)
          .getResultList();
      if (beforePosts.size() == 1) {
        nextBefore = posts.get(0).getPostId();
      }

      CriteriaQuery<Post> afterPostQuery = generateQuery(user.getUserId(),
          postSearchData.getPosterUserId(), postSearchData.getTitle(),
          postSearchData.getVisibility(),
          posts.get(posts.size() - 1).getPostId(), null, false);
      List<Post> afterPosts = entityManager.createQuery(afterPostQuery).setMaxResults(1)
          .getResultList();
      if (afterPosts.size() == 1) {
        nextAfter = posts.get(posts.size() - 1).getPostId();
      }
    }
    PageData pData = new PageData();
    pData.setPageItemsCount(numberPageItems);
    pData.setTotalCount(totalCount);
    if (nextAfter != null) {
      pData.setNext(buildNextPageUrl(nextAfter, postSearchData.getMaxRecordsCount(),
          postSearchData.getPosterUserId(), postSearchData.getTitle(),
          postSearchData.getVisibility()));
    }
    if (nextBefore != null) {
      pData.setPrevious(
          buildPreviousPageUrl(nextBefore, postSearchData.getMaxRecordsCount(),
              postSearchData.getPosterUserId(), postSearchData.getTitle(),
              postSearchData.getVisibility()));
    }
    return new PrevNextListPage<>(posts, pData);
  }

  private CriteriaQuery generateQuery(long userId, Long postUserId, String title,
      Visibility visibility, Long before, Long after, boolean countOnly) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery query;
    if (!countOnly) {
      query = cb.createQuery(Post.class);
    } else {
      query = cb.createQuery(Long.class);
    }
    if (!countOnly) {
      query.distinct(
          true); //for count: query must be select count (distinct column name), not select distinct column name...
    }
    Root<Post> root = query.from(Post.class);
    //Join from Post over User over Friend over User
    Join<Post, User> postUserJoin = root.join("user");
    Join<User, Friend> userFriendJoin1 = postUserJoin.join("friends1", JoinType.LEFT);
    Join<Friend, User> friendUserJoin2 = userFriendJoin1.join("user2", JoinType.LEFT);
    Join<User, Friend> userFriendJoin2 = postUserJoin.join("friends2", JoinType.LEFT);
    Join<Friend, User> friendUserJoin1 = userFriendJoin2.join("user1", JoinType.LEFT);
    //Predicates lists
    List<Predicate> predicatesOr1 = new LinkedList<>();
    List<Predicate> predicatesOr2 = new LinkedList<>();
    List<Predicate> predicatesOr3 = new LinkedList<>();
    List<Predicate> predicatesOr4 = new LinkedList<>();
    //Title condition
    Predicate titleCondition = null;
    if (title != null) {
      title = "%" + title.toLowerCase() + "%"; //substring of title is enough for a match
      titleCondition = cb.like(cb.lower(root.get(TITLE_KEY)), title);
    }
    if (before != null && after != null) {
      //Can have either before or after but not both set
      throw new IllegalArgumentException(
          messageSource.getMessage("PostDao.afterBeforeSetException"));
    }
    //Post user condition
    Predicate postUserCondition = null;
    if (postUserId != null && visibility != Visibility.ONLY_CURRENT_USER) {
      postUserCondition = cb.equal(postUserJoin.get(USER_ID), postUserId);
    }
    Predicate postAfterCondition = null;
    Predicate postBeforeCondition = null;
    //After postID condition, Before postID condition
    if (!countOnly) {
      if (after != null) {
        postAfterCondition = cb.greaterThan(root.get(POST_ID), after);
      } else if (before != null) {
        postBeforeCondition = cb.lessThan(root.get(POST_ID), before);
      }
    }
    Predicate[] predicatesOr1Array;
    //Visibility global => return all global posts
    if (visibility == Visibility.ONLY_GLOBAL) {
      predicatesOr1.add(cb.equal(root.get(IS_GLOBAL), true)); //filter on global posts
      if (titleCondition != null) {
        predicatesOr1.add(titleCondition); //filter on title
      }
      if (postUserCondition != null) {
        predicatesOr1.add(postUserCondition); //filter on userId of poster
      }
      if (!countOnly) {
        if (postAfterCondition != null) {
          predicatesOr1.add(postAfterCondition);
        } else if (postBeforeCondition != null) {
          predicatesOr1.add(postBeforeCondition);
        }
      }
      predicatesOr1Array = new Predicate[predicatesOr1.size()];
      predicatesOr1.toArray(predicatesOr1Array);
      query.where(
          cb.and(predicatesOr1Array)
      );
    }
    //Visibility current user
    else if (visibility == Visibility.ONLY_CURRENT_USER) { //postUserId is ignored
      predicatesOr1.add(cb.equal(postUserJoin.get(USER_ID), userId)); //filter on current user
      if (titleCondition != null) {
        predicatesOr1.add(titleCondition); //filter on title
      }
      if (!countOnly) {
        if (postAfterCondition != null) {
          predicatesOr1.add(postAfterCondition);
        } else if (postBeforeCondition != null) {
          predicatesOr1.add(postBeforeCondition);
        }
      }
      predicatesOr1Array = new Predicate[predicatesOr1.size()];
      predicatesOr1.toArray(predicatesOr1Array);
      query.where(
          cb.and(predicatesOr1Array)
      );
    }
    //Visibility is friend
    else if (visibility == Visibility.ONLY_FRIENDS) {
      //Visibility friend => filter friend posts and exclude current user posts
      //AND predicate 1
      predicatesOr1.add(cb.notEqual(postUserJoin.get(USER_ID), userId)); //exclude current user
      predicatesOr1.add(cb.equal(friendUserJoin2.get(USER_ID), userId)); //only friends
      predicatesOr1
          .add(cb.equal(userFriendJoin1.get(STATUS), FRIEND_ACCEPTED)); //status accepted
      predicatesOr1.add(cb.equal(root.get(IS_GLOBAL), false)); //no global posts
      //AND predicate 2
      predicatesOr2.add(cb.notEqual(postUserJoin.get(USER_ID), userId)); //exclude current user
      predicatesOr2.add(cb.equal(friendUserJoin1.get(USER_ID), userId)); //only friends
      predicatesOr2
          .add(cb.equal(userFriendJoin1.get(STATUS), FRIEND_ACCEPTED)); //status accepted
      predicatesOr2.add(cb.equal(root.get(IS_GLOBAL), false)); //no global posts
      //Other conditions like the title and the filter on the poster's userId
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
      if (countOnly) {
        query.where(friendsPredicate);
      } else {
        if (postAfterCondition != null) {
          query.where(
              cb.and(friendsPredicate, postAfterCondition)
          );
        } else if (postBeforeCondition != null) {
          query.where(
              cb.and(friendsPredicate, postBeforeCondition)
          );
        }
      }
    }
    //Visibility all: friends + global + this user
    else if (visibility == Visibility.ALL) {
      //Visibility friend => filter friend posts and exclude current user posts
      //OR predicate 1
      predicatesOr1.add(cb.equal(friendUserJoin2.get(USER_ID), userId)); //only friends
      predicatesOr1
          .add(cb.equal(userFriendJoin1.get(STATUS), FRIEND_ACCEPTED)); //status accepted
      //OR predicate 2
      predicatesOr2.add(cb.equal(friendUserJoin1.get(USER_ID), userId)); //only friends
      predicatesOr2
          .add(cb.equal(userFriendJoin1.get(STATUS), FRIEND_ACCEPTED)); //status accepted
      //OR predicate 3
      predicatesOr3.add(cb.equal(postUserJoin.get(USER_ID), userId)); //either current user
      //OR predicate 4
      predicatesOr4.add(cb.equal(root.get(IS_GLOBAL), true)); //either global post
      //Other conditions like the title and the filter on the poster's userId
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
      if (countOnly) {
        query.where(friendsUserGlobalPredicate);
      } else {
        if (postAfterCondition != null) {
          query.where(
              cb.and(friendsUserGlobalPredicate, postAfterCondition)
          );
        } else if (postBeforeCondition != null) {
          query.where(
              cb.and(friendsUserGlobalPredicate, postBeforeCondition)
          );
        }
      }
    }
    //Order by post ID and eventually limit
    if (countOnly) {
      query.select(cb.countDistinct(root));
    } else {
      if (after != null) {
        query.orderBy(cb.asc(root.get(POST_ID)));
      }
      //else before!=null || before==null (=> default to sort from most recent post to oldest post)
      else {
        query.orderBy(cb.desc(root.get(POST_ID)));
      }
    }
    return query;
  }

  private List<Map.Entry<String, String>> buildListKV(Long previous, Long next, Integer limit,
      Long postUserId, String title, Visibility visibility) {
    List<Map.Entry<String, String>> l = new ArrayList<>(5);
    Map.Entry<String, String> entry;
    if (postUserId != null) {
      entry = new AbstractMap.SimpleEntry<>(
          POSTER_USER_ID_KEY, String.valueOf(postUserId));
      l.add(entry);
    }
    if (title != null) {
      entry = new AbstractMap.SimpleEntry<>(TITLE_KEY, title);
      l.add(entry);
    }
    if (visibility != null) {
      entry = new AbstractMap.SimpleEntry<>(VISIBILITY_KEY, visibility.name());
      l.add(entry);
    }
    if (next != null) {
      entry = new AbstractMap.SimpleEntry<>(AFTER_KEY, String.valueOf(next));
      l.add(entry);
    }
    if (previous != null) {
      entry = new AbstractMap.SimpleEntry<>(BEFORE_KEY, String.valueOf(previous));
      l.add(entry);
    }
    if (limit != null) {
      entry = new AbstractMap.SimpleEntry<>(MAX_RECORDS_COUNT_KEY, String.valueOf(limit));
      l.add(entry);
    }
    return l;
  }

  private String buildNextPageUrl(Long next, Integer limit, Long postUserId, String title,
      Visibility visibility) {
    if (next == null || limit == null) {
      throw new IllegalArgumentException(
          messageSource.getMessage("PostDao.nextLimitNullException"));
    }
    return AppConfig.BASE_URL + PostsController.POSTS_PATH + "?" +
        StringUtilities.buildQueryStringFromListOfKVPairs(
            buildListKV(next, null, limit, postUserId, title, visibility));
  }

  private String buildPreviousPageUrl(Long previous, Integer limit, Long postUserId, String title,
      Visibility visibility) {
    if (previous == null || limit == null) {
      throw new IllegalArgumentException(
          messageSource.getMessage("PostDao.previousLimitNullException"));
    }
    return AppConfig.BASE_URL + PostsController.POSTS_PATH + "?" +
        StringUtilities.buildQueryStringFromListOfKVPairs(
            buildListKV(null, previous, limit, postUserId, title, visibility));
  }

  public Post getPostByUserAndLabel(Long userId, Long postUserId, String postShortTitle) {
    logger.debug("PostDao | getPostByUserAndLabel - userId: {}, postUserId: {}, postShortTitle: {}",
        userId, postUserId, postShortTitle);
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Post> query = cb.createQuery(Post.class);
    Root<Post> root = query.from(Post.class);
    Join<Post, User> postUserJoin = root.join("user", JoinType.LEFT);
    query.where(
        cb.and(
            cb.equal(root.get("shortTitle"), postShortTitle),
            cb.equal(postUserJoin.get(USER_ID), postUserId)
        )
    );
    try {
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}
