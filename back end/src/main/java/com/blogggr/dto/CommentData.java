package com.blogggr.dto;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
public class CommentData {

    private Long commentId;
    private String text;

    public CommentData(){
        //
    }


    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long postId) {
        this.commentId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
