package com.blogggr.strategies.responses;

import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 07.11.16.
 */
public abstract class GenericResponse implements ResponseStrategy {
    @Override
    public ResponseEntity notAuthorizedResponse(String errorMessage) {
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse(errorMessage), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity notAuthenticatedResponse(String errorMessage) {
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse(errorMessage), HttpStatus.UNAUTHORIZED);
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
    public abstract ResponseEntity successResponse(Object data);

    @Override
    public ResponseEntity notFound(String errorMessage){
        return new ResponseEntity(JSONResponseBuilder.generateErrorResponse(errorMessage), HttpStatus.NOT_FOUND);
    }
}
