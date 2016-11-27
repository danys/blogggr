package com.blogggr.dao;

import com.blogggr.entities.Friend;

import java.util.List;

/**
 * Created by Daniel Sunnen on 27.11.16.
 */
public interface FriendDAO extends GenericDAO<Friend> {

    List<Friend> getUserFriends(long userID);
}
