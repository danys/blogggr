package com.blogggr.strategies.validators;

import com.blogggr.requestdata.SessionPostData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class SessionPostDataValidator extends GenericValidator{

    public SessionPostDataValidator(){
        //super constructor called implicitly
    }

    @Override
    protected boolean validate(Map<String, String> input, String body) throws JsonProcessingException,JsonParseException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        SessionPostData sessionPostData = mapper.readValue(body, SessionPostData.class);
        //Check that all fields are present
        if (sessionPostData.getEmail()==null || sessionPostData.getPassword()==null || sessionPostData.getRememberMe()==null){
            errorMessage = "All fields need to be filled!";
            return false;
        }
        //Check that email looks somewhat like an email
        if (!sessionPostData.getEmail().contains("@")) {
            errorMessage = "E-mail address does not validate!";
            return false;
        }
        int atIndex = sessionPostData.getEmail().indexOf("@");
        int dotIndex = sessionPostData.getEmail().indexOf(".",atIndex);
        if (dotIndex==-1) {
            errorMessage = "E-mail address does not validate!";
            return false;
        }
        return true;
    }
}
