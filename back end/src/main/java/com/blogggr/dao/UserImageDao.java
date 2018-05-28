package com.blogggr.dao;

import com.blogggr.entities.UserImage;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
@Repository
public class UserImageDao extends GenericDAOImpl<UserImage>{

  public UserImageDao(){
    super(UserImage.class);
  }

  public UserImage findByName(String name) throws NoResultException{
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserImage> query = cb.createQuery(UserImage.class);
    Root<UserImage> root = query.from(UserImage.class);
    query.where(
              cb.equal(root.get("name"), name)
    );
    return entityManager.createQuery(query).getSingleResult();
  }

  public void unsetCurrent(Long userId){
    Query q = entityManager.createNativeQuery("UPDATE blogggr.user_images SET is_current = ? WHERE user_id = ?");
    q.setParameter(1, false);
    q.setParameter(2, userId);
    q.executeUpdate();
  }
}