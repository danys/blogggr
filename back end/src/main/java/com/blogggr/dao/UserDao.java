package com.blogggr.dao;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.UsersController;
import com.blogggr.entities.User;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PageData;
import com.blogggr.responses.PageMetaData;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.dto.UserSearchData;
import com.blogggr.utilities.SimpleBundleMessageSource;
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

  private final int defaultLimit = 50;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String SEARCH_KEY = "searchString";
  private static final String LIMIT_KEY = "limit";
  private static final String PAGE_NUMBER_KEY = "pageNumber";

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  public UserDao() {
    super(User.class);
  }

  public User findByIdWithImages(Long id) {
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> query = cb.createQuery(User.class);
      Root<User> root = query.from(User.class);
      root.fetch("userImages", JoinType.LEFT);
      query.where(cb.equal(root.get("userId"), id));
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(simpleBundleMessageSource
          .getMessage("UserDao.findByIdWithImages.notFound"));
    }
  }

  public RandomAccessListPage<User> getUsers(String searchString, Integer limit,
      Integer pageNumber) {
    //Check and maybe adjust limit, set default limit
    if (limit == null || limit.intValue() > defaultLimit) {
      limit = Integer.valueOf(defaultLimit);
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
    RandomAccessListPage<User> page = new RandomAccessListPage<>(users, pageMetaData);
    return page;
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
      query.orderBy(cb.asc(root.get("userId")));
    }
    return query;
  }

  public PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) {
    List<User> users = generateSearchTermQuery(searchData, User.class, true).getResultList();
    Long usersCountFiltered = generateSearchTermQuery(searchData, Long.class, true)
        .getSingleResult();
    Long usersCountAll = generateSearchTermQuery(searchData, Long.class, false).getSingleResult();
    PageData page = new PageData();
    page.setTotalCount(usersCountAll);
    page.setPageItemsCount(users.size());
    page.setFilteredCount(usersCountFiltered);
    return new PrevNextListPage(users, page);
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
    if (searchData.getBefore() != null) {
      beforeAfter = cb.lessThan(root.get("userId"), searchData.getBefore());
    } else if (searchData.getAfter() != null) {
      beforeAfter = cb.greaterThan(root.get("userId"), searchData.getAfter());
    }

    Predicate predicatesArray[];
    if (predicates.size() > 0) {
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
      query.orderBy(cb.asc(root.get("userId")));
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
