package com.blogggr.dao;

import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Daniel Sunnen on 27.11.16.
 */
public interface FriendDAO extends GenericDAO<Friend> {

    List<User> getUserFriends(long userID) throws ResourceNotFoundException, DBException;
}
