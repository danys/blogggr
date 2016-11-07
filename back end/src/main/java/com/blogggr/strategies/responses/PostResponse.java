package com.blogggr.strategies.responses;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.UsersController;
import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 05.11.16.
 *
 * Response to a POST request
 */
public class PostResponse extends GenericResponse implements ResponseStrategy {

    @Override
    public ResponseEntity successResponse(Object data) {
        //Type check
        if (!(data instanceof String))
            return new ResponseEntity(JSONResponseBuilder.generateErrorResponse("Class cast exception!"), HttpStatus.BAD_REQUEST);
        //Build response and return
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", (String) data);
        return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(""), headers, HttpStatus.CREATED);
    }
}
