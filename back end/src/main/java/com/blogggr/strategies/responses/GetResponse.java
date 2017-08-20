package com.blogggr.strategies.responses;

import com.blogggr.json.JSONResponseBuilder;
import com.blogggr.strategies.ResponseStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 07.11.16.
 *
 * Response to a GET request
 */
public class GetResponse extends GenericResponse implements ResponseStrategy {

  @Override
  public ResponseEntity successResponse(Object data) {
    return new ResponseEntity(JSONResponseBuilder.generateSuccessResponse(data), HttpStatus.OK);
  }
}
