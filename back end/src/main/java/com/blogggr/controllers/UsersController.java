package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.User;
import com.blogggr.services.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
