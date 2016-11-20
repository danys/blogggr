package com.blogggr.strategies.responses;

import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public class PutResponse extends GenericResponse implements ResponseStrategy {

    @Override
    public ResponseEntity successResponse(Object data) {
        return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(data), HttpStatus.OK);
    }
}