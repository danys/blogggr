package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public class CommentData {

    private Long commentID;
    private String text;

    public CommentData(){
        //
    }


    public Long getCommentID() {
        return commentID;
    }

    public void setCommentID(Long postID) {
        this.commentID = postID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
