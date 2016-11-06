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
@RequestMapping(AppConfig.baseUrl)
public class UsersController {

    public static final String userPath = "/users";

    private UserService userService;

    public UsersController(UserService userService){
        this.userService = userService;
    }

    //GET /users/id
    @RequestMapping(path = userPath+"/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        if (id==null) return null;
        return userService.getUserById(id);
    }

    //POST /users
    @RequestMapping(path = userPath, method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData){
        AppModel model = new AppModelImpl(new BasicAuthorization(),new UserPostDataValidator(), new InvokePostUserService(userService), new PostResponse());
        return model.execute(null,null,bodyData);
    }
}
