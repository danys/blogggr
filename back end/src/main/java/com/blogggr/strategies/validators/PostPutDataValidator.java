package com.blogggr.strategies.validators;

import com.blogggr.requestdata.PostData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.11.16.
 *
 * Class used to validate POST and PUT requests
 */
public class PostPutDataValidator extends GenericValidator{

    protected boolean validate(Map<String, String> input, String body) throws JsonParseException, JsonProcessingException, IOException{
        //Check that the post ID is present and is a number
        if (!input.containsKey(IdValidator.idName)){
            errorMessage = "Post id missing!";
            return false;
        }
        if (!stringIsNumber(input.get(IdValidator.idName))){
            errorMessage = "Post id not a valid number!";
            return false;
        }
        //Check the request body
        ObjectMapper mapper = new ObjectMapper();
        PostData postPostData = mapper.readValue(body, PostData.class);
        //Check that all fields are present
        if (postPostData.getTextBody()==null && postPostData.getTitle()==null && postPostData.getGlobal()==null){
            errorMessage = "At least one field must be filled!";
            return false;
        }
        //Check title length
        if (postPostData.getTitle()!=null && postPostData.getTitle().length()<=3){
            errorMessage = "Title is a little short!";
            return false;
        }
        //Check text length
        if (postPostData.getTextBody()!=null && postPostData.getTextBody().length()<=10){
            errorMessage = "Text is a little short!";
            return false;
        }
        return true;
    }
}
