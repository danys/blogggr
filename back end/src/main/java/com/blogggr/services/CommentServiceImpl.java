package com.blogggr.services;

import com.blogggr.dao.CommentDAO;
import com.blogggr.dao.FriendDAO;
import com.blogggr.dao.PostDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.CommentData;
import com.blogggr.utilities.TimeUtilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService{

    private CommentDAO commentDAO;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private FriendDAO friendDAO;

    public CommentServiceImpl(CommentDAO commentDAO, UserDAO userDAO, PostDAO postDAO, FriendDAO friendDAO){
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.friendDAO = friendDAO;
    }

    @Override
    public Comment createComment(long userID, CommentData commentData) throws ResourceNotFoundException, DBException, NotAuthorizedException {
        User user = userDAO.findById(userID);
        if (user==null) throw new ResourceNotFoundException("User not found!");
        Post post = postDAO.findById(commentData.getPostID());
        //If post is not global then user must be friends with the poster
        if (!post.getGlobal()){
            long posterUserID = post.getUser().getUserID();
            long smaller = (posterUserID<userID)?posterUserID:userID;
            long bigger = (posterUserID>=userID)?posterUserID:userID;
            try{
                friendDAO.getFriendByUserIDs(smaller, bigger);
            }
            catch(ResourceNotFoundException e){
                throw new NotAuthorizedException("User must be friends with poster as post is not globally visible!");
            }
        }
        if (post==null) throw new ResourceNotFoundException("Post not found!");
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(commentData.getText());
        comment.setTimestamp(TimeUtilities.getCurrentTimestamp());
        commentDAO.save(comment);
        return comment;
    }
}
