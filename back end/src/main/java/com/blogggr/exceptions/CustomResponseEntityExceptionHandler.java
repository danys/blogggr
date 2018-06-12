package com.blogggr.exceptions;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by Daniel Sunnen on 11.06.18.
 */
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @ExceptionHandler(value = Exception.class)
  protected ResponseEntity handleGenericException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.other.error");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  protected ResponseEntity handleIllegalArgumentException(RuntimeException ex) {
    logger.error(ex.getMessage());
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = ResourceNotFoundException.class)
  protected ResponseEntity handleResourceNotFoundException(RuntimeException ex) {
    logger.error(ex.getMessage(), ex);
    return ResponseBuilder.errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = UsernameNotFoundException.class)
  protected ResponseEntity handleUsernameNotFoundException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.authentication.userNotFound");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.UNAUTHORIZED);
  }

  //All Database exceptions of Repository beans translated by Spring into instances of DataAccessException

  @ExceptionHandler(value = NonTransientDataAccessException.class)
  protected ResponseEntity handleNonTransientDataAccessException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.nonTransientExceptionError");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = RecoverableDataAccessException.class)
  protected ResponseEntity handleRecoverableDataAccessException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.recoverableExceptionError");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = ScriptException.class)
  protected ResponseEntity handleScriptException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.scriptExceptionError");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = TransientDataAccessResourceException.class)
  protected ResponseEntity handleTransientDataAccessResourceException(RuntimeException ex) {
    String message = simpleBundleMessageSource.getMessage("exception.db.transientExceptionError");
    logger.error(message, ex);
    return ResponseBuilder.errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
