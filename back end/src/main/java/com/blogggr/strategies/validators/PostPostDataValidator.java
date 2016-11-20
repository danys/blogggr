package com.blogggr.strategies.validators;

import com.blogggr.requestdata.PostPostData;
import com.blogggr.strategies.ValidationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public class PostPostDataValidator extends GenericValidator{

    protected boolean validate(Map<String, String> input, String body) throws JsonParseException, JsonProcessingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        PostPostData postPostData = mapper.readValue(body, PostPostData.class);
        //Check that all fields are present
        if (postPostData.getTextBody()==null || postPostData.getTitle()==null){
            errorMessage = "All fields need to be filled!";
            return false;
        }
        //Check title length
        if (postPostData.getTitle().length()<=3){
            errorMessage = "Title is a little short!";
            return false;
        }
        //Check text length
        if (postPostData.getTextBody().length()<=10){
            errorMessage = "Text is a little short!";
            return false;
        }
        return true;
    }
}
