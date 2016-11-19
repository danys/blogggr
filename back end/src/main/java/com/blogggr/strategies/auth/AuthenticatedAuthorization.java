package com.blogggr.strategies.auth;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.User;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.services.UserService;
import com.blogggr.strategies.AuthorizationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 07.11.16.
 * Only extracts the userID from the current session
 */
public class AuthenticatedAuthorization implements AuthorizationStrategy {

    private UserService userService;
    private User authenticatedUser;
    private boolean dbCheck;

    private String errorMessage;

    public AuthenticatedAuthorization(UserService userService){
        this.userService = userService;
        dbCheck = false;
        errorMessage = "";
    }

    @Override
    public boolean isAuthorized(Map<String,String> header){
        //Extract the Authorization value
        if (!header.containsKey(AppConfig.headerAuthorizationKey)) {
            errorMessage = "Not authenticated!";
            return false;
        }
        String authHash = header.get(AppConfig.headerAuthorizationKey);
        //Check if the session hash is assigned to any user
        User user;
        try{
            user = userService.getUserBySessionHash(authHash);
        }
        catch(SessionExpiredException e){
            errorMessage = "Session is expired!";
            return false;
        }
        dbCheck = true; //executed DB call to find the current user
        if (user==null) {
            errorMessage = "Invalid session token!";
            return false;
        }
        authenticatedUser = user;
        return true;
    }

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

    @Override
    public String getError(){
        return errorMessage;
    }
}
