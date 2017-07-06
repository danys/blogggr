package com.blogggr.strategies.validators;

import com.blogggr.requestdata.UserPostData;
import com.blogggr.requestdata.UserPutData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
public class UserPutDataValidator extends GenericValidator{

    protected boolean validate(Map<String, String> input, String body) throws JsonParseException, JsonProcessingException, IOException {
        //Check if id is present
        if (!input.containsKey(IdValidator.idName)){
            errorMessage = "User id not provided!";
            return false;
        }
        if (!stringIsNumber(input.get(IdValidator.idName))){
            errorMessage = "User must be identified by its id!";
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        UserPutData userData = mapper.readValue(body, UserPutData.class);
        //Check the validity of the body parameters
        if (userData.getPassword()!=null && userData.getPasswordRepeat()==null){
            errorMessage = "Provide the new password twice!";
            return false;
        }
        if (userData.getFirstName()==null
            && userData.getLastName()==null
            && userData.getEmail()==null
            && userData.getPassword()==null){
            errorMessage = "At least one field must be provided!";
            return false;
        }
        //Check other conditions
        UserPostData temp = new UserPostData();
        temp.setFirstName(userData.getFirstName());
        temp.setLastName(userData.getLastName());
        String valRes = UserPostDataValidator.validateUserData(temp);
        if (!valRes.isEmpty()) {
            errorMessage = valRes;
            return false;
        }
        return true;
    }
}
