package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 18.11.16.
 */
public class NotAuthorizedException extends Exception{
    public NotAuthorizedException(String message){
        super(message);
    }
}