package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class FriendData {

    private Long userId1;
    private Long userId2;
    private Integer action;

    public FriendData(){
        //
    }

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
