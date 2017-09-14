package com.blogggr.dao;

import com.blogggr.entities.UserImage;
import com.blogggr.entities.UserImage_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
@Repository
public class UserImageDAOImpl extends GenericDAOImpl<UserImage> implements UserImageDAO{

  public UserImageDAOImpl(){
    super(UserImage.class);
  }

  @Override
  public UserImage findByName(String name){
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserImage> query = cb.createQuery(UserImage.class);
    Root<UserImage> root = query.from(UserImage.class);
    query.where(
              cb.equal(root.get(UserImage_.name), name)
    );
    return entityManager.createQuery(query).getSingleResult();
  }
}