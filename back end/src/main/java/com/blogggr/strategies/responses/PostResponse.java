package com.blogggr.strategies.responses;

import com.blogggr.config.AppConfig;
import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 05.11.16.
 *
 * Response to a POST request
 */
public class PostResponse extends GenericResponse implements ResponseStrategy {

    @Override
    public ResponseEntity successResponse(Object data) {
        //Type check
        if (!(data instanceof Map))
            return new ResponseEntity(JSONResponseBuilder.generateErrorResponse("Class cast exception!"), HttpStatus.BAD_REQUEST);
        //Build response and return
        HttpHeaders headers = new HttpHeaders();
        Map<String,String> map = (Map<String,String>) data;
        headers.add(AppConfig.locationHeaderKey, map.get(AppConfig.locationHeaderKey));
        //Optional auth data in the response body: "{Auth: <sessionHash>,ValidUntil: <ts>}"
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode authData = null;
        if (map.containsKey(AppConfig.authKey) && map.containsKey(AppConfig.validityUntilKey) && map.containsKey(AppConfig.emailKey)) {
            authData = factory.objectNode();
            authData.put(AppConfig.authKey,map.get(AppConfig.authKey));
            authData.put(AppConfig.validityUntilKey,map.get(AppConfig.validityUntilKey));
            authData.put(AppConfig.emailKey,map.get(AppConfig.emailKey));
        }
        return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(authData), headers, HttpStatus.CREATED);
    }
}
