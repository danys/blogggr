package com.blogggr.dao;

import com.blogggr.entities.User;
import com.blogggr.entities.User_;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.models.RandomAccessListPage;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
@Repository
public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO{

    public static final String noUserFound = "User not found!";
    public static final String dbException = "Database exception!";

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

    public RandomAccessListPage<User> getUsers(String searchString){
        //TODO
        return null;
    }
}
