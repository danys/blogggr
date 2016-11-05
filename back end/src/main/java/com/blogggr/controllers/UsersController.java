package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.User;
import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.models.*;
import com.blogggr.services.UserService;
import com.blogggr.validator.UserDataValidator;
import com.blogggr.validator.UserPostDataValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 24.10.16.
 */
@RestController
@RequestMapping("/api/v"+ AppConfig.apiVersion)
public class UsersController {

    private UserService userService;

    public UsersController(UserService userService){
        this.userService = userService;
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        if (id==null) return null;
        return userService.getUserById(id);
    }

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData){
        /*UserDataValidator validator = new UserDataValidator(userData);
        if (!validator.validate()) return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(validator.getErrorMessage()), HttpStatus.BAD_REQUEST);
        User user = null;
        try{
            user = userService.storeUser(validator);
        }
        catch(Exception e){
            return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        String userIDStr = String.valueOf(user.getUserID());
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            responseHeaders.setLocation(new URI("http://127.0.0.1:8080/users/"+userIDStr));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(userIDStr),responseHeaders,HttpStatus.ACCEPTED);*/
        AppModel model = new AppModelImpl(new BasicAuthorization(),new UserPostDataValidator(), new InvokePostUserService(userService), new PostResponse());
        return model.execute(null,null,bodyData);
    }
}
