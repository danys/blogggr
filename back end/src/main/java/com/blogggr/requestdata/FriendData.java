package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class FriendData {

    private Long userID1;
    private Long userID2;
    private Integer action;

    public FriendData(){
        //
    }

    public Long getUserID1() {
        return userID1;
    }

    public void setUserID1(Long userID1) {
        this.userID1 = userID1;
    }

    public Long getUserID2() {
        return userID2;
    }

    public void setUserID2(Long userID2) {
        this.userID2 = userID2;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
