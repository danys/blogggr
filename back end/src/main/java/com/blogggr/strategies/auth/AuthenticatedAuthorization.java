package com.blogggr.strategies.auth;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 07.11.16.
 */
public class AuthenticatedAuthorization {

    public AuthenticatedAuthorization(){
        //
    }

    public boolean isAuthorized(Map<String,String> header){
        //TODO: Check that user has an active session
        return true;
    }

    public Long getUserId(Map<String,String> header){
        //TODO: retrieve the user's userID from his session
        return null;
    }
}
