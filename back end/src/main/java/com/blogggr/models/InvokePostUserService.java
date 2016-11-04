package com.blogggr.models;

import com.blogggr.requestdata.UserPostData;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
public class InvokePostUserService implements ServiceInvocationStrategy{

    public InvokePostUserService(){
        //
    }

    public Object invokeService(Map<String,String> input, String body){
        user = userService.createUser(validator);

        ObjectMapper mapper = new ObjectMapper();
        UserPostData userData = null;
        try{
            userData = mapper.readValue(body, UserPostData.class);
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
        catch(JsonParseException e){
            return false;
        }
        catch(JsonProcessingException e){
            return false;
        }
        catch(IOException e){
            return false;
        }

    }

}
