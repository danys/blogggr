package com.blogggr.responses;

import com.blogggr.config.AppConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Daniel Sunnen on 07.06.18.
 */
public class ResponseBuilder {

  private ResponseBuilder() {
  }

  //HTTP GET OK response
  public static ResponseEntity getSuccessResponse(Object data) {
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, data),
        HttpStatus.OK);
  }

  //HTTP POST CREATED response
  public static ResponseEntity postSuccessResponse(String location) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AppConfig.locationHeaderKey, location);
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, null), headers,
        HttpStatus.CREATED);
  }

  //HTTP POST CREATED response with data
  public static ResponseEntity postSuccessResponseWithData(String location, Object data) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AppConfig.locationHeaderKey, location);
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, data), headers,
        HttpStatus.CREATED);
  }

  //HTTP PUT OK response
  public static ResponseEntity putSuccessResponse() {
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, null), HttpStatus.OK);
  }

  //HTTP DELETE OK response
  public static ResponseEntity deleteSuccessResponse() {
    return new ResponseEntity(new DataResponseBody(AppConfig.apiVersion, null), HttpStatus.OK);
  }
}
