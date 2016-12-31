package com.blogggr.dao;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Daniel Sunnen on 27.10.16.
 */
public interface PostDAO extends GenericDAO<Post>{

    //Post getUserPosts(long userID, int nRowsPerPage, int pageNum);

    //Post getPostByUserIDandPostID(long userID, long postID, int nRowsPerPage, int pageNum);

    List<Post> getPosts(long userID, Long postUserID, String title, PostDAOImpl.Visibility visibility, Long before, Long after, Integer limit) throws DBException, ResourceNotFoundException;
}
