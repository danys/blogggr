package com.blogggr.dao;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.requestdata.UserSearchData;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public interface UserDAO extends GenericDAO<User> {

  User getUserByEmail(String email) throws DBException, ResourceNotFoundException;

  RandomAccessListPage<User> getUsers(String searchString, Integer limit, Integer pageNumber)
      throws DBException;

  PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) throws DBException;
}
