package com.blogggr.exceptions;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by Daniel Sunnen on 11.06.18.
 */
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final Logger logging = LoggerFactory.getLogger(this.getClass());

  /**
   * Helper method called in other methods of this class
   */
  private ResponseEntity logAndRespond(String messageCode, Exception ex, HttpStatus httpStatus){
    String message = simpleBundleMessageSource.getMessage(messageCode);
    logging.error(message, ex);
    return ResponseBuilder.errorResponse(message, httpStatus);
  }

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleGenericException(RuntimeException ex) {
    return logAndRespond("exception.other.error", ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity handleIllegalArgumentException(RuntimeException ex) {
    return logAndRespond(ex.getMessage(), ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity handleResourceNotFoundException(RuntimeException ex) {
    return logAndRespond(ex.getMessage(), ex, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity handleUsernameNotFoundException(RuntimeException ex) {
    return logAndRespond("exception.authentication.userNotFound", ex, HttpStatus.UNAUTHORIZED);
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
    return logAndRespond("exception.controller.methodArgumentNotValid", ex, HttpStatus.BAD_REQUEST);
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
    return logAndRespond("exception.controller.bindException", ex, HttpStatus.BAD_REQUEST);
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
}
