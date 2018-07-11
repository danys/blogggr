package com.blogggr.responses;

import com.blogggr.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
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
    return new ResponseEntity(new DataResponseBody(AppConfig.API_VERSION, data),
        HttpStatus.OK);
  }

  //HTTP POST CREATED response
  public static ResponseEntity postSuccessResponse(String location) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AppConfig.LOCATION_HEADER_KEY, location);
    return new ResponseEntity(new DataResponseBody(AppConfig.API_VERSION, null), headers,
        HttpStatus.CREATED);
  }

  //HTTP POST CREATED response with data
  public static ResponseEntity postSuccessResponseWithData(String location, Object data) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AppConfig.LOCATION_HEADER_KEY, location);
    return new ResponseEntity(new DataResponseBody(AppConfig.API_VERSION, data), headers,
        HttpStatus.CREATED);
  }

  //HTTP PUT OK response
  public static ResponseEntity putSuccessResponse() {
    return new ResponseEntity(new DataResponseBody(AppConfig.API_VERSION, null), HttpStatus.OK);
  }

  //HTTP DELETE OK response
  public static ResponseEntity deleteSuccessResponse() {
    return new ResponseEntity(new DataResponseBody(AppConfig.API_VERSION, null), HttpStatus.OK);
  }

  //Error response with a single error message
  public static ResponseEntity errorResponse(String errorMessage, HttpStatus httpStatus) {
    return new ResponseEntity(
        new ErrorResponseBody(AppConfig.API_VERSION, Arrays.asList(errorMessage)), httpStatus);
  }

  //Error response with a list of error messages
  public static ResponseEntity errorResponse(List<String> errorMessages, HttpStatus httpStatus) {
    return new ResponseEntity(new ErrorResponseBody(AppConfig.API_VERSION, errorMessages),
        httpStatus);
  }

  public static ResponseEntity fieldsErrorResponse(List<FieldError> fieldErrors, HttpStatus httpStatus){
    return new ResponseEntity(new FieldsErrorResponseBody(AppConfig.API_VERSION, fieldErrors),
        httpStatus);
  }

  public static void filterResponse(HttpServletResponse response, String message,
      HttpStatus httpStatus) throws IOException {
    response.setStatus(httpStatus.value());
    response.setHeader("Content-Type", "application/json;charset=UTF-8");
    String json = new ObjectMapper()
        .writeValueAsString(new ErrorResponseBody(AppConfig.API_VERSION,
            Arrays.asList(message)));
    response.getWriter().write(json);
  }

  @Getter
  @Setter
  public static class FieldError{
    private String fieldName;
    private String message;

    public FieldError(String fieldName, String message){
      this.fieldName = fieldName;
      this.message = message;
    }
  }
}
