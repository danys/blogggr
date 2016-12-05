package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public class CommentData {

    private Long postID;
    private String text;

    public CommentData(){
        //
    }


    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
