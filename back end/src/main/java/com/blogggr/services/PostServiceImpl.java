package com.blogggr.services;

import com.blogggr.dao.PostDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.PostPostData;
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

    //Compacts a title. Truncation occurs if the title is too long
    private String compactTitle(String title){
        title = title.toLowerCase();
        StringBuilder sb = new StringBuilder();
        char[] chars = title.toCharArray();
        int len=0;
        for(int i=0;i<chars.length;i++){
            if (chars[i]!=' ') {sb.append(chars[i]);len++;}
            else {sb.append('-');len++;}
            if (len>30) break;
        }
        return sb.toString();
    }

    @Override
    public Post createPost(long userID, PostPostData postData) throws ResourceNotFoundException{
        User user = userDAO.findById(userID);
        if (user==null) throw new ResourceNotFoundException("User not found!");
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postData.getTitle());
        post.setTextbody(postData.getTextBody());
        post.setTimestamp(TimeUtilities.getCurrentTimestamp());
        post.setShorttitle(compactTitle(postData.getTitle()));
        postDAO.save(post);
        return post;
    }
}
