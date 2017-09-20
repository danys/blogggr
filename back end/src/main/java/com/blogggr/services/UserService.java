package com.blogggr.services;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.requestdata.UserPutData;
import com.blogggr.requestdata.UserSearchData;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
public interface UserService {

  User getUserById(long id);

  User getUserByIdWithImages(long id);

  User createUser(UserPostData userData);

  User getUserByEmail(String email) throws ResourceNotFoundException, DBException;

  void updateUser(long userResourceID, long userID, UserPutData userData)
      throws ResourceNotFoundException, DBException, NotAuthorizedException;

  RandomAccessListPage<User> getUsers(String searchString, Integer limit, Integer pageNumber)
      throws DBException;

  PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) throws DBException;
}
