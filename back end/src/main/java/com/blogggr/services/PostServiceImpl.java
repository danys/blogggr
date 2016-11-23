package com.blogggr.services;

import com.blogggr.dao.PostDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.PostData;
import com.blogggr.utilities.StringUtilities;
import com.blogggr.utilities.TimeUtilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl implements PostService{

    private PostDAO postDAO;
    private UserDAO userDAO;

    public PostServiceImpl(PostDAO postDAO, UserDAO userDAO){
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }



    @Override
    public Post createPost(long userID, PostData postData) throws ResourceNotFoundException{
        User user = userDAO.findById(userID);
        if (user==null) throw new ResourceNotFoundException("User not found!");
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postData.getTitle());
        post.setTextbody(postData.getTextBody());
        post.setTimestamp(TimeUtilities.getCurrentTimestamp());
        post.setShorttitle(StringUtilities.compactTitle(postData.getTitle()));
        postDAO.save(post);
        return post;
    }

    @Override
    public Post updatePost(long postID, long userID, PostData postData) throws ResourceNotFoundException, NotAuthorizedException{
        Post post = postDAO.findById(postID);
        if (post==null) throw new ResourceNotFoundException("Post not found!");
        if (post.getUser().getUserID()!=userID) throw new NotAuthorizedException("No authorization to modify this post!");
        //Update timestamp
        post.setTimestamp(TimeUtilities.getCurrentTimestamp());
        if (postData.getTextBody()!=null) post.setTextbody(postData.getTextBody());
        if (postData.getTitle()!=null) {
            post.setTitle(postData.getTitle());
            post.setShorttitle(StringUtilities.compactTitle(postData.getTitle()));
        }
        postDAO.save(post);
        return post;
    }
}
