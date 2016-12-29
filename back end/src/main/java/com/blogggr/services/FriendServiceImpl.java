package com.blogggr.services;

import com.blogggr.dao.FriendDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Friend;
import com.blogggr.entities.FriendPK;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.requestdata.FriendData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FriendServiceImpl implements FriendService{

    private UserDAO userDAO;
    private FriendDAO friendDAO;

    public FriendServiceImpl(UserDAO userDAO, FriendDAO friendDAO){
        this.userDAO = userDAO;
        this.friendDAO = friendDAO;
    }

    @Override
    public Friend createFriend(long userID, FriendData friendData) throws ResourceNotFoundException, NotAuthorizedException {
        long userID1 = friendData.getUserID1();
        long userID2 = friendData.getUserID2();
        if (userID1!=userID && userID2!=userID) throw new NotAuthorizedException("Current user must be a part of the new friendship!");
        else if (userID1==userID2) throw new NotAuthorizedException("Can not be befriended with oneself!");
        long small, big;
        small = (userID1<userID2)?userID1:userID2;
        big = (userID1>userID2)?userID1:userID2;
        //Fetch users
        User userSmall = userDAO.findById(small);
        if (userSmall==null) throw new ResourceNotFoundException("User not found!");
        User userBig = userDAO.findById(big);
        if (userBig==null) throw new ResourceNotFoundException("User not found!");
        Friend friend = new Friend();
        friend.setUser1(userSmall);
        friend.setUser2(userBig);
        friend.setStatus(0); //initial status => pending friendship state
        if (userSmall.getUserID()==userID) friend.setUser3(userSmall);
        else friend.setUser3(userBig);
        FriendPK fPk = new FriendPK();
        fPk.setuserOneID(userSmall.getUserID());
        fPk.setuserTwoID(userBig.getUserID());
        friend.setId(fPk);
        friendDAO.save(friend);
        return friend;
    }

    @Override
    public void updateFriend(long userID, long user1, long user2, FriendData friendData) throws ResourceNotFoundException, NotAuthorizedException, DBException{
        if (user1!=userID && user2!=userID) throw new NotAuthorizedException("Current user must be a part of the new friendship!");
        long small, big;
        small = (user1<user2)?user1:user2;
        big = (user1>user2)?user1:user2;
        Friend friend = friendDAO.getFriendByUserIDs(small,big);
        if (friend==null) throw new ResourceNotFoundException("Friendship not found!");
        if (friend.getUser1().getUserID()!=userID && friend.getUser2().getUserID()!=userID) throw new NotAuthorizedException("Not authorized to change this friendship!");
        User currentUser = userDAO.findById(userID);
        if (currentUser==null) throw new ResourceNotFoundException("User not found!");
        if (friend.getStatus()==0 && friendData.getAction()!=0){ //accept friend request
            if (friend.getUser3().getUserID()!=userID){
                //user at one side sets status to pending the other user can set it to 1, 2 or 3
                friend.setStatus(friendData.getAction());
                friend.setUser3(currentUser);
                friendDAO.update(friend);
            }
            else throw new NotAuthorizedException("Cannot update friend status!");
        }
        else if (friend.getStatus()==1 && friendData.getAction()==3){ //set status to blocked
            friend.setStatus(friendData.getAction());
            friend.setUser3(currentUser);
            friendDAO.update(friend);
        }
        else if ((friend.getStatus()==2 || friend.getStatus()==3) && friendData.getAction()==1) { //set status to accepted from declined or blocked
            if (friend.getUser3().getUserID()==userID){
                friend.setStatus(friendData.getAction());
                friend.setUser3(currentUser);
                friendDAO.update(friend);
            }
        }
        else throw new NotAuthorizedException("Cannot update friend status!");
    }

    @Override
    public void deleteFriend(long friendId, long userID) throws ResourceNotFoundException, NotAuthorizedException{
        Friend friend = friendDAO.findById(friendId);
        if (friend==null) throw new ResourceNotFoundException("Friendship not found");
        if (friend.getUser1().getUserID()!=userID || friend.getUser2().getUserID()!=userID) throw new NotAuthorizedException("Not authorized to delete friendship!");
        friendDAO.delete(friend);
    }

    @Override
    public List<User> getFriends(long userID) throws ResourceNotFoundException, DBException{
        List<User> friends = friendDAO.getUserFriends(userID);
        return friends;
    }
}
