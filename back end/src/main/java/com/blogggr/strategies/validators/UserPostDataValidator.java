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

    public static String validateUserData(UserPostData userData){
        if (userData.getFirstName().length()<3 || userData.getLastName().length()<3 || userData.getPassword().length()<8) {
            return "First name and last name must have at least 3 characters. Password must have 8 characters!";
        }
        if (!userData.getEmail().contains("@")) {
            return "E-mail address does not validate!";
        }
        int atIndex = userData.getEmail().indexOf("@");
        int dotIndex = userData.getEmail().indexOf(".",atIndex);
        if (dotIndex==-1) {
            return "E-mail address does not validate!";
        }
        if (userData.getPasswordRepeat().compareTo(userData.getPassword())!=0) {
            return "Submitted passwords must match!";
        }
        return "";
    }

    @Override
    protected boolean validate(Map<String,String> input, String body) throws JsonProcessingException,JsonParseException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        UserPostData userData = mapper.readValue(body, UserPostData.class);
        if (userData.getFirstName() == null || userData.getLastName() == null || userData.getEmail() == null || userData.getPassword() == null) {
            errorMessage = "Provide all required fields!";
            return false;
        }
        String valRes = validateUserData(userData);
        if (!valRes.isEmpty()) {
            errorMessage = valRes;
            return false;
        }
        return true;
    }

}
