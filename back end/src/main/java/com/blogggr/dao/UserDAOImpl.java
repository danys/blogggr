package com.blogggr.dao;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.PostsController;
import com.blogggr.controllers.UsersController;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.entities.User_;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.PageMetaData;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.strategies.validators.GetUsersValidator;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
@Repository
public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO{

    public static final String noUserFound = "User not found!";
    public static final String dbException = "Database exception!";

    private final int defaultLimit = 50;

    public UserDAOImpl(){
        super(User.class);
    }

    public User getUserByEmail(String email) throws DBException, ResourceNotFoundException{
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.where(cb.equal(root.get(User_.email), email));
            return entityManager.createQuery(query).getSingleResult();
        }
        catch(NoResultException e){
            throw new ResourceNotFoundException(noUserFound);
        }
        catch(Exception e){
            throw new DBException(dbException);
        }
    }

    public RandomAccessListPage<User> getUsers(String searchString, Integer limit, Integer pageNumber) throws DBException{
        try{
            //Check and maybe adjust limit, set default limit
            if (limit == null) limit = Integer.valueOf(defaultLimit);
            else if (limit.intValue() > defaultLimit) limit = Integer.valueOf(defaultLimit);
            if (pageNumber==null || pageNumber<0) pageNumber = 1;
            //Offset
            int offset = (pageNumber-1)*limit;
            //Queries
            CriteriaQuery<User> userQuery = generateQuery(searchString, false);
            CriteriaQuery<Long> userCountQuery = generateQuery(searchString, true);
            //Fetches
            List<User> users = entityManager.createQuery(userQuery).setFirstResult(offset).setMaxResults(limit).getResultList();
            Long count = entityManager.createQuery(userCountQuery).getSingleResult();
            PageMetaData pageMetaData = new PageMetaData();
            pageMetaData.setTotalCount(count);
            int nPages = (int)((count/limit)+1);
            pageMetaData.setnPages(nPages);
            pageMetaData.setPageCount(pageNumber);
            //TODO and add limit and pageNumber to input hash map
            pageMetaData.setPageUrl(AppConfig.fullBaseUrl + UsersController.userPath + "?"+ GetUsersValidator.searchKey+"="+searchString+"");
            RandomAccessListPage<User> page = new RandomAccessListPage<>(users,pageMetaData);
            return page;
        }
        catch(Exception e){
            throw new DBException("Database exception!");
        }
    }

    private CriteriaQuery generateQuery(String searchString, boolean countOnly){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query;
        if (!countOnly) query = cb.createQuery(User.class);
        else query = cb.createQuery(Long.class);
        if (!countOnly) query.distinct(true); //for count: query must be select count (distinct column name), not select distinct column name...
        Root<User> root = query.from(User.class);

        String searchVar = "%"+searchString+"%";
        query.where(
                cb.or(
                        cb.like(cb.lower(root.get(User_.email)),searchVar),
                        cb.like(cb.lower(root.get(User_.firstName)),searchVar),
                        cb.like(cb.lower(root.get(User_.lastName)),searchVar)
                )
        );
        if (countOnly) query.select(cb.countDistinct(query.from(User.class)));
        else query.orderBy(cb.asc(root.get(User_.userID)));
        return query;
    }
}
