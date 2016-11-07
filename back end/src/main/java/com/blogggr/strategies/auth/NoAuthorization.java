package com.blogggr.strategies.auth;

import com.blogggr.strategies.AuthorizationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 07.11.16.
 */
public class NoAuthorization implements AuthorizationStrategy {

    public NoAuthorization(){
        //
    }

    public boolean isAuthorized(Map<String,String> header){
        return true;
    }

    public Long getUserId(Map<String,String> header){
        return null;
    }
}