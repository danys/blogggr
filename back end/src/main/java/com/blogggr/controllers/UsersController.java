package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.*;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.auth.NoAuthorization;
import com.blogggr.strategies.invoker.InvokeGetUserService;
import com.blogggr.strategies.invoker.InvokePostUserService;
import com.blogggr.strategies.invoker.InvokePutUserService;
import com.blogggr.strategies.responses.GetResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.GetUsersValidator;
import com.blogggr.strategies.validators.IdValidator;
import com.blogggr.strategies.validators.UserPostDataValidator;
import com.blogggr.strategies.validators.UserPutDataValidator;
import com.blogggr.utilities.HTTPMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    //GET /users
    @RequestMapping(path = userPath, method = RequestMethod.GET)
    public ResponseEntity getUsers(@RequestParam Map<String,String> params, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new GetUsersValidator(), new InvokeGetUsersService(userService), new GetResponse());
        return model.execute(params,header,null);
    }

    //GET /users/id
    @RequestMapping(path = userPath+"/{id}", method = RequestMethod.GET)
    public ResponseEntity getUser(@PathVariable String id, @RequestHeader Map<String,String> header) {
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeGetUserService(userService), new GetResponse());
        return model.execute(map,header,null);
    }

    //POST /users
    @RequestMapping(path = userPath, method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData){
        AppModel model = new AppModelImpl(new NoAuthorization(), new UserPostDataValidator(), new InvokePostUserService(userService), new PostResponse());
        return model.execute(null,null,bodyData);
    }

    //PUT /users/id
    @RequestMapping(path = userPath+"/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@PathVariable String id,@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new UserPutDataValidator(), new InvokePutUserService(userService), new PutResponse());
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        return model.execute(map,header,bodyData);
    }
}
