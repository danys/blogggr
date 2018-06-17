package com.blogggr.exceptions;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by Daniel Sunnen on 11.06.18.
 */
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final Logger logging = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleGenericException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.other.error");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity handleIllegalArgumentException(RuntimeException ex) {
    logging.error(ex.getMessage());
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity handleResourceNotFoundException(RuntimeException ex) {
    logging.error(ex.getMessage(), ex);
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity handleUsernameNotFoundException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.authentication.userNotFound");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.UNAUTHORIZED);
  }

  //All Database exceptions of Repository beans translated by Spring into instances of DataAccessException

  @ExceptionHandler(NonTransientDataAccessException.class)
  public ResponseEntity handleNonTransientDataAccessException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.nonTransientExceptionError");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(RecoverableDataAccessException.class)
  public ResponseEntity handleRecoverableDataAccessException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.recoverableExceptionError");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ScriptException.class)
  public ResponseEntity handleScriptException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.scriptExceptionError");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(TransientDataAccessResourceException.class)
  public ResponseEntity handleTransientDataAccessResourceException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.transientExceptionError");
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Custom response for NoHandlerFoundException.class which is treated in the parent class
   * and delegates to this method
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    String message = simpleBundleMessageSource.getMessage("exception.controller.noHandlerFound");
    logging.error(message + " Exception message: " + ex.getMessage());
    return ResponseBuilder.errorResponse(message, HttpStatus.NOT_FOUND);
  }

  /**
   * HTTP verb not implemented exception handling
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    String message = simpleBundleMessageSource.getMessage("exception.controller.noHandlerFoundHttpVerb");
    logging.error(message + " Exception message: " + ex.getMessage());
    return ResponseBuilder.errorResponse(message, HttpStatus.NOT_IMPLEMENTED);
  }
}
