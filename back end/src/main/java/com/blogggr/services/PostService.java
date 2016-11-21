package com.blogggr.services;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.PostData;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public interface PostService {

    Post createPost(long userID, PostData postData) throws ResourceNotFoundException;

    Post updatePost(long postID, long userID, PostData postData) throws ResourceNotFoundException, NotAuthorizedException;
}
