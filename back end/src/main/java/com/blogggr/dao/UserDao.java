package com.blogggr.dao;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.UsersController;
import com.blogggr.entities.User;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PageMetaData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.dto.UserSearchData;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.StringUtilities;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
@Repository
public class UserDao extends GenericDaoImpl<User> {

  private static final int DEFAULT_LIMIT = 50;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String SEARCH_KEY = "searchString";
  private static final String LIMIT_KEY = "limit";
  private static final String PAGE_NUMBER_KEY = "pageNumber";
  private static final String USER_ID = "userId";

  private static final String FIRST_NAME_KEY = "firstName";
  private static final String LAST_NAME_KEY = "lastName";
  private static final String EMAIL_KEY = "emailName";
  private static final String MAX_RECORDS_COUNT_KEY = "maxRecordsCount";
  private static final String BEFORE_KEY = "before";
  private static final String AFTER_KEY = "after";


  @Autowired
  private SimpleBundleMessageSource messageSource;

  public UserDao() {
    super(User.class);
  }

  public User findByIdWithImages(Long id) {
    logger.debug("UserDao | findByIdWithImages - id: {}", id);
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> query = cb.createQuery(User.class);
      Root<User> root = query.from(User.class);
      root.fetch("userImages", JoinType.LEFT);
      query.where(cb.equal(root.get(USER_ID), id));
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public RandomAccessListPage<User> getUsers(String searchString, Integer limit,
      Integer pageNumber) {
    logger.debug("UserDao | getUsers - searchString: {}, limit: {}, pageNumber: {}", searchString,
        limit, pageNumber);
    //Check and maybe adjust limit, set default limit
    if (limit == null || limit.intValue() > DEFAULT_LIMIT) {
      limit = Integer.valueOf(DEFAULT_LIMIT);
    }
    if (pageNumber == null || pageNumber < 0) {
      pageNumber = 1;
    }
    //Offset
    int offset = (pageNumber - 1) * limit;
    //Queries
    CriteriaQuery<User> userQuery = generateQuery(searchString, false);
    CriteriaQuery<Long> userCountQuery = generateQuery(searchString, true);
    //Fetches
    List<User> users = entityManager.createQuery(userQuery).setFirstResult(offset)
        .setMaxResults(limit).getResultList();
    Long count = entityManager.createQuery(userCountQuery).getSingleResult();
    //Create the page meta data
    PageMetaData pageMetaData = new PageMetaData();
    pageMetaData.setTotalCount(count);
    int nPages = (count % limit == 0) ? (int) (count / limit) : (int) ((count / limit) + 1);
    pageMetaData.setNPages(nPages);
    pageMetaData.setPageId(pageNumber);
    pageMetaData.setPageItemsCount(users.size());
    StringBuilder sb = new StringBuilder();
    sb.append(AppConfig.FULL_BASE_URL);
    sb.append(UsersController.USER_PATH);
    sb.append("?");
    if (searchString != null && searchString.length() > 0) {
      sb.append(SEARCH_KEY);
      sb.append("=");
      sb.append(searchString);
      sb.append("&");
    }
    sb.append(PAGE_NUMBER_KEY);
    sb.append("=");
    sb.append(Integer.toString(pageNumber));
    sb.append("&");
    sb.append(LIMIT_KEY);
    sb.append("=");
    sb.append(Integer.toString(limit));
    pageMetaData.setPageUrl(sb.toString());
    return new RandomAccessListPage<>(users, pageMetaData);
  }

  private CriteriaQuery generateQuery(String searchString, boolean countOnly) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery query;
    if (!countOnly) {
      query = cb.createQuery(User.class);
    } else {
      query = cb.createQuery(Long.class);
    }
    if (!countOnly) {
      query.distinct(
          true); //for count: query must be select count (distinct column name), not select distinct column name...
    }
    Root<User> root = query.from(User.class);
    if (searchString != null && searchString.length() > 0) {
      String searchVar = "%" + searchString.toLowerCase() + "%";
      query.where(
          cb.or(
              cb.like(cb.lower(root.get("email")), searchVar),
              cb.like(cb.lower(root.get("firstName")), searchVar),
              cb.like(cb.lower(root.get("lastName")), searchVar)
          )
      );
    }
    if (countOnly) {
      query.select(cb.countDistinct(root));
    } else {
      query.distinct(true);
      query.orderBy(cb.asc(root.get(USER_ID)));
    }
    return query;
  }

  public PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) {
    logger.debug("UserDao | getUsersBySearchTerms - searchData : {}", searchData);
    List<User> users = generateSearchTermQuery(searchData, User.class, true).getResultList();
    Long usersCountFiltered = generateSearchTermQuery(searchData, Long.class, true)
        .getSingleResult();
    Long usersCountAll = generateSearchTermQuery(searchData, Long.class, false).getSingleResult();
    PageData page = new PageData();
    page.setTotalCount(usersCountAll);
    page.setPageItemsCount(users.size());
    page.setFilteredCount(usersCountFiltered);
    //Figure out if a user is before or after the users of this page
    Long nextAfter = null;
    Long nextBefore = null;
    if (page.getFilteredCount() > 0 && !users.isEmpty()) {
      //Get the user before the first one
      UserSearchData prevUserSearchData = new UserSearchData();
      prevUserSearchData.setBefore(users.get(0).getUserId());
      prevUserSearchData.setFirstName(searchData.getFirstName());
      prevUserSearchData.setLastName(searchData.getLastName());
      prevUserSearchData.setEmail(searchData.getEmail());
      prevUserSearchData.setMaxRecordsCount(1);
      TypedQuery<User> beforeUserQuery = generateSearchTermQuery(prevUserSearchData, User.class,
          true);
      List<User> beforeUsers = beforeUserQuery
          .getResultList();
      if (beforeUsers.size() == 1) {
        nextBefore = users.get(0).getUserId();
      }
      //Get the user after the last one
      UserSearchData afterUserSearchData = new UserSearchData();
      afterUserSearchData.setAfter(users.get(users.size() - 1).getUserId());
      afterUserSearchData.setFirstName(searchData.getFirstName());
      afterUserSearchData.setLastName(searchData.getLastName());
      afterUserSearchData.setEmail(searchData.getEmail());
      afterUserSearchData.setMaxRecordsCount(1);
      TypedQuery<User> afterUserQuery = generateSearchTermQuery(afterUserSearchData, User.class,
          true);
      List<User> afterUsers = afterUserQuery
          .getResultList();
      if (afterUsers.size() == 1) {
        nextAfter = users.get(users.size()-1).getUserId();
      }
    }
    if (nextAfter != null) {
      page.setNext(buildNextPageUrl(searchData, nextAfter));
    }
    if (nextBefore != null) {
      page.setPrevious(buildPreviousPageUrl(searchData, nextBefore));
    }
    return new PrevNextListPage(users, page);
  }

  private String buildNextPageUrl(UserSearchData userSearchData, Long afterUserId) {
    UserSearchData nextSearchData = new UserSearchData();
    nextSearchData.setEmail(userSearchData.getEmail());
    nextSearchData.setFirstName(userSearchData.getFirstName());
    nextSearchData.setLastName(userSearchData.getLastName());
    nextSearchData.setMaxRecordsCount(userSearchData.getMaxRecordsCount());
    nextSearchData.setBefore(null);
    nextSearchData.setAfter(afterUserId);
    return AppConfig.BASE_URL + UsersController.USER_PATH + "?" +
        StringUtilities.buildQueryStringFromListOfKVPairs(
            buildListKV(nextSearchData));
  }

  private String buildPreviousPageUrl(UserSearchData userSearchData, Long beforeUserId) {
    UserSearchData nextSearchData = new UserSearchData();
    nextSearchData.setEmail(userSearchData.getEmail());
    nextSearchData.setFirstName(userSearchData.getFirstName());
    nextSearchData.setLastName(userSearchData.getLastName());
    nextSearchData.setMaxRecordsCount(userSearchData.getMaxRecordsCount());
    nextSearchData.setBefore(beforeUserId);
    nextSearchData.setAfter(null);
    return AppConfig.BASE_URL + UsersController.USER_PATH + "?" +
        StringUtilities.buildQueryStringFromListOfKVPairs(
            buildListKV(nextSearchData));
  }

  private List<Map.Entry<String, String>> buildListKV(UserSearchData userSearchData) {
    List<Map.Entry<String, String>> l = new ArrayList<>(5);
    Map.Entry<String, String> entry;
    if (userSearchData.getFirstName() != null) {
      entry = new AbstractMap.SimpleEntry<>(FIRST_NAME_KEY, userSearchData.getFirstName());
      l.add(entry);
    }
    if (userSearchData.getLastName() != null) {
      entry = new AbstractMap.SimpleEntry<>(LAST_NAME_KEY, userSearchData.getLastName());
      l.add(entry);
    }
    if (userSearchData.getEmail() != null) {
      entry = new AbstractMap.SimpleEntry<>(EMAIL_KEY, userSearchData.getEmail());
      l.add(entry);
    }
    if (userSearchData.getAfter() != null) {
      entry = new AbstractMap.SimpleEntry<>(AFTER_KEY, String.valueOf(userSearchData.getAfter()));
      l.add(entry);
    }
    if (userSearchData.getBefore() != null) {
      entry = new AbstractMap.SimpleEntry<>(BEFORE_KEY, String.valueOf(userSearchData.getBefore()));
      l.add(entry);
    }
    if (userSearchData.getMaxRecordsCount() != null) {
      entry = new AbstractMap.SimpleEntry<>(MAX_RECORDS_COUNT_KEY,
          String.valueOf(userSearchData.getMaxRecordsCount()));
      l.add(entry);
    }
    return l;
  }

  private <T> TypedQuery<T> generateSearchTermQuery(UserSearchData searchData, Class<T> resultClass,
      boolean doFilter) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery query =
        (resultClass != Long.class) ? cb.createQuery(User.class) : cb.createQuery(Long.class);
    Root<User> root = query.from(User.class);
    if (resultClass != Long.class) {
      root.fetch("userImages", JoinType.LEFT);
    }
    List<Predicate> predicates = new LinkedList<>();
    if (doFilter) {
      if (searchData.getFirstName() != null) {
        predicates.add(cb.like(cb.lower(root.get("firstName")),
            searchData.getFirstName().toLowerCase() + "%"));
      }
      if (searchData.getLastName() != null) {
        predicates.add(cb.like(cb.lower(root.get("lastName")),
            searchData.getLastName().toLowerCase() + "%"));
      }
      if (searchData.getEmail() != null) {
        predicates.add(
            cb.like(cb.lower(root.get("email")), searchData.getEmail().toLowerCase() + "%"));
      }
    }

    Predicate beforeAfter = null;
    //Before and after cannot be set at the same time
    if (searchData.getBefore() != null && resultClass != Long.class) {
      beforeAfter = cb.lessThan(root.get(USER_ID), searchData.getBefore());
    } else if (searchData.getAfter() != null && resultClass != Long.class) {
      beforeAfter = cb.greaterThan(root.get(USER_ID), searchData.getAfter());
    }

    Predicate[] predicatesArray;
    if (!predicates.isEmpty()) {
      predicatesArray = new Predicate[predicates.size()];
      predicates.toArray(predicatesArray);

      if (beforeAfter != null) {
        query.where(
            cb.and(
                cb.and(predicatesArray),
                beforeAfter
            )
        );
      } else {
        query.where(
            cb.and(
                cb.and(predicatesArray)
            )
        );
      }
    } else if (beforeAfter != null) {
      query.where(beforeAfter);
    }
    if (resultClass != Long.class) {
      query.orderBy(cb.asc(root.get(USER_ID)));
    } else {
      query.select(cb.count(root));
    }
    TypedQuery tQuery = entityManager.createQuery(query);
    if (resultClass != Long.class) {
      tQuery.setMaxResults(searchData.getMaxRecordsCount());
    }
    return tQuery;
  }
}
