package com.blogggr.services;

import com.blogggr.entities.Comment;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public interface CommentService {

    Comment createComment(long userID, CommentData commentData) throws ResourceNotFoundException, DBException, NotAuthorizedException;
}
