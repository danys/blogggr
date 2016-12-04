package com.blogggr.services;

import com.blogggr.dao.FriendDAO;
import com.blogggr.dao.PostDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.PostData;
import com.blogggr.utilities.StringUtilities;
import com.blogggr.utilities.TimeUtilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl implements PostService{

    private PostDAO postDAO;
    private UserDAO userDAO;
    private FriendDAO friendDAO;

    private final String postNotFound = "Post not found!";
    private final String noModifyAuthorization = "No authorization to modify this post!";
    private final String noReadAuthorization = "No authorization to view this post!";

    public PostServiceImpl(PostDAO postDAO, UserDAO userDAO, FriendDAO friendDAO){
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.friendDAO = friendDAO;
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
        post.setGlobal(postData.getGlobal());
        postDAO.save(post);
        return post;
    }

    @Override
    public Post updatePost(long postID, long userID, PostData postData) throws ResourceNotFoundException, NotAuthorizedException{
        Post post = postDAO.findById(postID);
        if (post==null) throw new ResourceNotFoundException(postNotFound);
        if (post.getUser().getUserID()!=userID) throw new NotAuthorizedException(noModifyAuthorization);
        //Update timestamp
        post.setTimestamp(TimeUtilities.getCurrentTimestamp());
        if (postData.getTextBody()!=null) post.setTextbody(postData.getTextBody());
        if (postData.getTitle()!=null) {
            post.setTitle(postData.getTitle());
            post.setShorttitle(StringUtilities.compactTitle(postData.getTitle()));
        }
        if (postData.getGlobal()!=null) post.setGlobal(postData.getGlobal());
        postDAO.save(post);
        return post;
    }

    //Delete a session by its primary key
    @Override
    public void deletePost(long postId, long userID) throws ResourceNotFoundException, NotAuthorizedException{
        Post post = postDAO.findById(postId);
        if (post==null) throw new ResourceNotFoundException(postNotFound);
        if (post.getUser().getUserID()!=userID) throw new NotAuthorizedException(noModifyAuthorization);
        postDAO.deleteById(postId);
    }

    //Simple function to check that this user is friends with the poster
    private boolean isFriendOfUser(long postUserID, long userID) throws DBException{
        try {
            long smallNum, bigNum;
            smallNum = (postUserID < userID) ? postUserID : userID;
            bigNum = (postUserID >= userID) ? postUserID : userID;
            friendDAO.getFriendByUserIDs(smallNum, bigNum);
            return true;
        }
        catch(ResourceNotFoundException e){
            return false;
        }
    }

    @Override
    public Post getPostById(long postId, long userID) throws ResourceNotFoundException, DBException, NotAuthorizedException {
        Post post = postDAO.findById(postId);
        if (post==null) throw new ResourceNotFoundException(postNotFound);
        User postAuthor = post.getUser();
        //1. Post can be viewed if current session user is the owner or the post has global flag
        if (postAuthor.getUserID()==userID || post.getGlobal()) return post;
        //2. Post can be viewed if the current user is friends with the poster
        if (isFriendOfUser(postAuthor.getUserID(),userID)) return post;
        //Otherwise access denied
        throw new NotAuthorizedException(noReadAuthorization);
    }


}
