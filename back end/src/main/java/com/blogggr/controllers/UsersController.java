package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.User;
import com.blogggr.services.UserService;
import com.blogggr.validator.UserDataValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity createUser(Map<String, String> userData){
        UserDataValidator validator = new UserDataValidator(userData);
        if (!validator.validate()) return new ResponseEntity<String>(validator.getErrorMessage(), HttpStatus.BAD_REQUEST);
        User user = new User();
        user.setFirstName(validator.getFirstName());
        user.setLastName(validator.getLastName());
        user.setEmail(validator.getEmail());
        //TODO encrypt password
        user.setPasswordHash(validator.getPassword());
        //save to db
        userService.storeUser(user);
    }
}
