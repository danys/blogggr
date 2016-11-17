package com.blogggr.strategies.auth;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.UsersController;
import com.blogggr.entities.User;
import com.blogggr.services.UserService;
import com.blogggr.strategies.AuthorizationStrategy;
import com.blogggr.utilities.HTTPMethod;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public class BasicAuthorization implements AuthorizationStrategy {

    private String requestPath;
    private HTTPMethod method;

    private UserService userService;
    private boolean dbCheck;
    private User authenticatedUser;

    public BasicAuthorization(String requestPath, HTTPMethod method, UserService userService){
        this.requestPath = requestPath;
        this.method = method;
        this.userService = userService;
        this.dbCheck = false;
    }

    @Override
    public boolean isAuthorized(Map<String,String> header){
        //Extract the Authorization value
        if (!header.containsKey(AppConfig.headerAuthorizationKey)) return false;
        String authHash = header.get(AppConfig.headerAuthorizationKey);
        //Check user has an active session
        User user = userService.getUserBySessionHash(authHash);
        dbCheck = true; //executed DB call to find the current user
        if (user==null) return false;
        authenticatedUser = user;
        //Check that the user is allowed to view content
        if (requestPath.indexOf(UsersController.userPath)==0) return true //TODO
        return true;
    }

    //Fetch the user assigned to the current session
    @Override
    public Long getUserId(Map<String,String> header){
        if (dbCheck) {
            if (authenticatedUser==null) return null;
            else return authenticatedUser.getUserID();
        }
        //The DB has not yet been queried, do it now
        isAuthorized(header);
        if (authenticatedUser==null) return null;
        return authenticatedUser.getUserID();
    }
}
