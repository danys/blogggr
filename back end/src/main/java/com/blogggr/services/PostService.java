package com.blogggr.services;

import com.blogggr.entities.Post;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.PostPostData;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public interface PostService {

    Post createPost(long userID, PostPostData postData) throws ResourceNotFoundException;
}
