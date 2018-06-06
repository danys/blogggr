package com.blogggr.responses;

import com.blogggr.config.AppConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 07.06.18.
 */
public class ResponseBuilder {

  private ResponseBuilder(){}

  public static ResponseEntity successResponse(Object data){
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, data),
        HttpStatus.OK);
  }
}
