package com.blogggr.dao;

import com.blogggr.entities.UserImage;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
@Repository
public class UserImageDao extends GenericDaoImpl<UserImage> {

  public UserImageDao() {
    super(UserImage.class);
  }

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public UserImage findByName(String name) {
    logger.debug("UserImageDao | findbyName - name: {}", name);
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserImage> query = cb.createQuery(UserImage.class);
    Root<UserImage> root = query.from(UserImage.class);
    query.where(
        cb.equal(root.get("name"), name)
    );
    try {
      return entityManager.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public List<UserImage> findUserImagesByUserId(Long userId) {
    logger.debug("UserImageDao | findUserImagesByUserId - userId: {}", userId);
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserImage> query = cb.createQuery(UserImage.class);
    Root<UserImage> root = query.from(UserImage.class);
    query.where(
        cb.equal(root.get("user").get("userId"), userId)
    );
    return entityManager.createQuery(query).getResultList();
  }

  public int unsetCurrent(Long userId) {
    logger.debug("UserImageDao | unsetCurrent - userId: {}", userId);
    List<UserImage> userImages = findUserImagesByUserId(userId);
    int nRecordsModified = 0;
    for(UserImage userImage: userImages){
      if (userImage.getIsCurrent()){
        userImage.setIsCurrent(false);
        nRecordsModified++;
      }
    }
    return nRecordsModified;
  }
}