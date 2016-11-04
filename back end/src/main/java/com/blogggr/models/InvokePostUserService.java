package com.blogggr.models;

import com.blogggr.strategies.ServiceInvocationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
public class InvokePostUserService implements ServiceInvocationStrategy{

    public InvokePostUserService(){
        //
    }

    public Object invokeService(Map<String,String> input, String body){
        user = userService.storeUser(validator);
    }

}
