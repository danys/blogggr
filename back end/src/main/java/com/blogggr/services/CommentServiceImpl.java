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

import java.util.List;

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

    private final String notCommentFound = "Comment not found!";

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

    @Override
    public void updateComment(long commentID, long userID, CommentData commentData) throws ResourceNotFoundException, NotAuthorizedException{
        Comment comment = commentDAO.findById(commentID);
        if (comment==null) throw new ResourceNotFoundException(notCommentFound);
        if (comment.getUser().getUserID()!=userID) throw new NotAuthorizedException("Not allowed to change comment!");
        comment.setTimestamp(TimeUtilities.getCurrentTimestamp()); //update timestamp
        comment.setText(commentData.getText());
    }

    @Override
    public void deleteComment(long commentID, long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException{
        try{
            Comment comment = commentDAO.findById(commentID);
            if (comment==null) throw new ResourceNotFoundException(notCommentFound);
            if (comment.getUser().getUserID()!=userID) throw new NotAuthorizedException("Not allowed to delete comment!");
            commentDAO.deleteById(commentID);
        }
        catch(Exception e){
            throw new DBException("Database exception deleting comment!");
        }
    }

    @Override
    public Comment getCommentById(long commentID, long userID) throws ResourceNotFoundException{
        Comment comment = commentDAO.findById(commentID);
        //Everybody can read the comment
        if (comment==null) throw new ResourceNotFoundException(notCommentFound);
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(long postID, long userID) throws ResourceNotFoundException, NotAuthorizedException, DBException{
        //Fetch the post first
        Post post = postDAO.findById(postID);
        if (post==null) throw new ResourceNotFoundException("Did not find post!");
        User postAuthor = post.getUser();
        //1. Comments of the post can be viewed if current session user is the owner or the post has global flag
        if (postAuthor.getUserID()==userID || post.getGlobal()) return post.getComments();
        //2. Post and comments can be viewed if the current user is friends with the poster
        long smallNum, bigNum;
        smallNum = (postAuthor.getUserID() < userID) ? postAuthor.getUserID() : userID;
        bigNum = (postAuthor.getUserID() >= userID) ? postAuthor.getUserID() : userID;
        try{
            friendDAO.getFriendByUserIDs(smallNum, bigNum);
            return post.getComments();
        }
        catch(ResourceNotFoundException e){
            throw new NotAuthorizedException("Not allowed to view this post and its comments!");
        }
    }
}
