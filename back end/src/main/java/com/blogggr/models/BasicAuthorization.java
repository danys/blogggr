package com.blogggr.models;

import com.blogggr.strategies.AuthorizationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public class BasicAuthorization implements AuthorizationStrategy {

    public BasicAuthorization(){
        //
    }

    public boolean isAuthorized(Map<String,String> header){
        return true;
    }

    public Long getUserId(Map<String,String> header){
        return null;
    }
}
