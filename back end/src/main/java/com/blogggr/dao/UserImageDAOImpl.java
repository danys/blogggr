package com.blogggr.dao;

import com.blogggr.entities.UserImage;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
@Repository
public class UserImageDAOImpl extends GenericDAOImpl<UserImage> implements UserImageDAO{

  public UserImageDAOImpl(){
    super(UserImage.class);
  }
}