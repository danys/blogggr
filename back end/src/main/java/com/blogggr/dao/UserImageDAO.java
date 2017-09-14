package com.blogggr.dao;

import com.blogggr.entities.UserImage;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
public interface UserImageDAO extends GenericDAO<UserImage>{

  UserImage findByName(String name);
}
