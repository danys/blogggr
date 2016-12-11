package com.blogggr.services;

import com.blogggr.entities.Comment;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public interface CommentService {

    Comment createComment(long userID, CommentData commentData) throws ResourceNotFoundException, DBException, NotAuthorizedException;

    void updateComment(long commentID, long userID, CommentData commentData) throws ResourceNotFoundException, NotAuthorizedException;

    void deleteComment(long commentID, long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException;

    Comment getCommentById(long commentID, long userID) throws ResourceNotFoundException;

    List<Comment> getCommentsByPostId(long postID, long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException;
}
