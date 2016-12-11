package com.blogggr.strategies.validators;

import com.blogggr.requestdata.CommentData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class CommentPutDataValidator extends GenericValidator{

    protected boolean validate(Map<String, String> input, String body) throws JsonParseException, JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CommentData postCommentData = mapper.readValue(body, CommentData.class);
        //Only check that the text field is present, ignore the post id field
        if  (postCommentData.getText()==null){
            errorMessage = "Submit the new comment text!";
            return false;
        }
        //Check text length
        if (postCommentData.getText().length()<=3){
            errorMessage = "Comment is a little short!";
            return false;
        }
        return true;
    }
}