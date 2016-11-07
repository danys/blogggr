package com.blogggr.strategies.auth;

import com.blogggr.strategies.AuthorizationStrategy;
import com.blogggr.utilities.HTTPMethod;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public class BasicAuthorization implements AuthorizationStrategy {

    private String requestPath;
    private HTTPMethod method;

    public BasicAuthorization(String requestPath, HTTPMethod method){
        this.requestPath = requestPath;
        this.method = method;
    }

    public boolean isAuthorized(Map<String,String> header){
        //TODO: Check user has an active session and is allowed to view content
        return true;
    }

    public Long getUserId(Map<String,String> header){
        //TODO: Retrieve the user's userID assigned to his current active session
        return null;
    }
}
