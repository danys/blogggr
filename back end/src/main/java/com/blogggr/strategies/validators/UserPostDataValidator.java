package com.blogggr.strategies.validators;

import com.blogggr.requestdata.UserPostData;
import com.blogggr.strategies.ValidationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
public class UserPostDataValidator extends GenericValidator {

    public UserPostDataValidator(){
        //
    }

    @Override
    protected boolean validate(Map<String,String> input, String body) throws JsonProcessingException,JsonParseException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        UserPostData userData = mapper.readValue(body, UserPostData.class);
        if (userData.getFirstName() == null || userData.getLastName() == null || userData.getEmail() == null || userData.getPassword() == null) {
            errorMessage = "Provide all required fields!";
            return false;
        }
        if (userData.getFirstName().length()<3 || userData.getLastName().length()<3 || userData.getPassword().length()<8) {
            errorMessage = "First name and last name must have at least 3 characters!";
            return false;
        }
        if (!userData.getEmail().contains("@")) {
            errorMessage = "E-mail address does not validate!";
            return false;
        }
        int atIndex = userData.getEmail().indexOf("@");
        int dotIndex = userData.getEmail().indexOf(".",atIndex);
        if (dotIndex==-1) {
            errorMessage = "E-mail address does not validate!";
            return false;
        }
        if (userData.getPasswordRepeat().compareTo(userData.getPassword())!=0) {
            errorMessage = "Submitted passwords must match!";
            return false;
        }
        return true;
    }

}
