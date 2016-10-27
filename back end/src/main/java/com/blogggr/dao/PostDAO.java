package com.blogggr.dao;

import com.blogggr.entities.Post;

/**
 * Created by Daniel Sunnen on 27.10.16.
 */
public interface PostDAO extends GenericDAO<Post>{

    Post getUserPosts(long userID, int nRowsPerPage, int pageNum);

    Post getPostByUserIDandPostID(long userID, long postID, int nRowsPerPage, int pageNum);
    
}
