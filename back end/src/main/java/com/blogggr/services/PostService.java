package com.blogggr.services;

import com.blogggr.dao.PostDAOImpl;
import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.requestdata.PostData;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public interface PostService {

    Post createPost(long userID, PostData postData) throws ResourceNotFoundException;

    Post updatePost(long postID, long userID, PostData postData) throws ResourceNotFoundException, NotAuthorizedException;

    void deletePost(long postId, long userID) throws ResourceNotFoundException, NotAuthorizedException;

    Post getPostById(long postId, long userID) throws ResourceNotFoundException, DBException, NotAuthorizedException;

    PrevNextListPage<Post> getPosts(long userID, Long postUserID, String title, PostDAOImpl.Visibility visibility, Long before, Long after, Integer limit) throws ResourceNotFoundException, DBException;

    Post getPostByUserAndLabel(Long userID, Long postUserID, String postShortTitle) throws ResourceNotFoundException, DBException, NotAuthorizedException;
}
