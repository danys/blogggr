package com.blogggr.exceptions;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
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

  /**
   * Helper method called in other methods of this class
   */
  private ResponseEntity logAndRespond(String messageCode, Exception ex, HttpStatus httpStatus){
    String message = simpleBundleMessageSource.getMessage(messageCode);
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, httpStatus);
  }

  private ResponseEntity logAndSimpleResponse(Exception ex, HttpStatus httpStatus){
    logging.error(ex.getMessage(), ex);
    return ResponseBuilder.errorResponse(ex.getMessage(), httpStatus);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleGenericException(RuntimeException ex) {
    return logAndRespond("exception.other.error", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity handleIllegalArgumentException(RuntimeException ex) {
    return logAndSimpleResponse(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity handleUsernameNotFoundException(RuntimeException ex) {
    return logAndRespond("exception.authentication.userNotFound", ex, HttpStatus.UNAUTHORIZED);
  }

  //Application specific exceptions
  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity handleNotAuthorizedException(RuntimeException ex) {
    return logAndSimpleResponse(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity handleStorageException(RuntimeException ex) {
    return logAndSimpleResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity handleResourceNotFoundException(RuntimeException ex) {
    return logAndSimpleResponse(ex, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity handleMessagingException(MessagingException e){
    return logAndRespond("UserService.createUser.emailException", e, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //All Database exceptions of Repository beans translated by Spring into instances of DataAccessException

  @ExceptionHandler(NonTransientDataAccessException.class)
  public ResponseEntity handleNonTransientDataAccessException(RuntimeException ex) {
    return logAndRespond("exception.db.nonTransientExceptionError", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(RecoverableDataAccessException.class)
  public ResponseEntity handleRecoverableDataAccessException(RuntimeException ex) {
    return logAndRespond("exception.db.recoverableExceptionError", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ScriptException.class)
  public ResponseEntity handleScriptException(RuntimeException ex) {
    return logAndRespond("exception.db.scriptExceptionError", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(TransientDataAccessResourceException.class)
  public ResponseEntity handleTransientDataAccessResourceException(RuntimeException ex) {
    return logAndRespond("exception.db.transientExceptionError", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
  public ResponseEntity handleUnsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException ex) {
    logging.error(ex.getLocalizedMessage(), ex);
    return ResponseBuilder.errorResponse(ex.getLocalizedMessage().replaceAll("\\{|\\}", "").replace('\"', '\''), HttpStatus.BAD_REQUEST);
  }

  /**
   * Custom response for NoHandlerFoundException.class which is treated in the parent class
   * and delegates to this method
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.noHandlerFound", ex, HttpStatus.NOT_FOUND);
  }

  /**
   * HTTP verb not implemented exception handling
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.noHandlerFoundHttpVerb", ex, HttpStatus.METHOD_NOT_ALLOWED);
  }



  /**
   * Customize the response for HttpMediaTypeNotSupportedException.
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.mediaTypeNotSupported", ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * Customize the response for HttpMediaTypeNotAcceptableException.
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.mediaTypeNotAcceptable", ex, HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * Customize the response for MissingPathVariableException.
   */
  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.missingPathVariable", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Customize the response for MissingServletRequestParameterException.
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.missingServletRequestParameter", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for ServletRequestBindingException.
   */
  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.servletRequestBindingException", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for ConversionNotSupportedException.
   */
  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.conversionNotSupported", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Customize the response for TypeMismatchException.
   */
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.typeMismatch", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for HttpMessageNotReadableException.
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.httpMessageNotReadable", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for HttpMessageNotWritableException.
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.httpMessageNotWritable", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Customize the response for MethodArgumentNotValidException.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    List<com.blogggr.responses.ResponseBuilder.FieldError> errors = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(new com.blogggr.responses.ResponseBuilder.FieldError(error.getField(),simpleBundleMessageSource.getMessage(error.getCodes())));
    }
    logging.error(ex.getMessage(), ex);
    return ResponseBuilder.fieldsErrorResponse(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for MissingServletRequestPartException.
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.controller.missingServletRequestPart", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for BindException.
   */
  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<com.blogggr.responses.ResponseBuilder.FieldError> errors = new ArrayList<>();
    for(FieldError fieldError: fieldErrors){
      errors.add(new ResponseBuilder.FieldError(fieldError.getField(), simpleBundleMessageSource.getMessage(fieldError.getCodes())));
    }
    logging.error(ex.getMessage(), ex);
    return ResponseBuilder.fieldsErrorResponse(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Customize the response for NoHandlerFoundException.
   */
  @Override
  protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
      AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
    return logAndRespond("exception.controller.asyncRequestTimeoutException", ex, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * For all other exceptions
   */
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return logAndRespond("exception.other.error", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Constraint validation handler
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity handleConstraintViolation(
      ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(violation.getRootBeanClass().getName() + " " +
          violation.getPropertyPath() + ": " + violation.getMessage());
    }
    logging.error("Validation exception", ex);
    return ResponseBuilder.errorResponse(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Type mismatch of a method argument
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    logging.error("Argument type mismatch exception", ex);
    return ResponseBuilder.errorResponse(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
  }

}
