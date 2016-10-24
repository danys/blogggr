package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Sunnen on 24.10.16.
 */
@RestController
@RequestMapping("/api/v"+ AppConfig.apiVersion)
public class UsersController {

    @RequestMapping("/users", method = RequestMethod.POST)
    public String createUser(@RequestBody UserResource userResource) {

        return new String("Hello world");
    }
}
