package com.blogggr.models;

import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 05.11.16.
 */
public class PostResponse implements ResponseStrategy {
    @Override
    public ResponseEntity notAuthorizedResponse() {
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse("Not authorized!"), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity invalidInputResponse(String errorMessage) {
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity exceptionResponse(String exceptionMessage) {
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse(exceptionMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity successResponse(Object data) {
        return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(data), HttpStatus.ACCEPTED);
    }
}
