package com.blogggr.json;

import com.blogggr.config.AppConfig;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by Daniel Sunnen on 30.10.16.
 */
public class JSONResponseBuilder {

    public static ObjectNode generateErrorResponse(String errorMessage){
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("apiVersion", AppConfig.apiVersion);
        root.put("error",errorMessage);
        return root;
    }

    public static ObjectNode generateSuccessResponse(Object data){
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("apiVersion", AppConfig.apiVersion);
        root.putPOJO("data",data);
        return root;
    }
}
