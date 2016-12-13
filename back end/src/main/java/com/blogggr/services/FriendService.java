package com.blogggr.services;

import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.FriendData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public interface FriendService {

    Friend createFriend(long userID, FriendData friendData) throws ResourceNotFoundException, DBException, NotAuthorizedException;

    void updateFriend(long userID, long user1, long user2, FriendData friendData) throws ResourceNotFoundException, NotAuthorizedException, DBException;

    void deleteFriend(long friendId, long userID) throws ResourceNotFoundException, NotAuthorizedException;

    List<User> getFriends(long userID) throws ResourceNotFoundException, DBException;
}