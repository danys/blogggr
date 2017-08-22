package com.blogggr.models;

import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.strategies.AuthorizationStrategy;
import com.blogggr.strategies.ResponseStrategy;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.ValidationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptException;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public class AppModelImpl implements AppModel {

  private final String duplicateKeyError = "Unique key violation";
  private final String exceptionError = "Exceptional error";
  private final String nonTransientExceptionError = "Non-transient database error.";
  private final String recoverableExceptionError = "Recoverable database error, please retry.";
  private final String scriptExceptionError = "Error processing SQL.";
  private final String transientExceptionError = "Transient database error, please retry.";
  private final String wrongPasswordError = "Wrong password!";

  private AuthorizationStrategy authBehavior;
  private ValidationStrategy validationBehavior;
  private ServiceInvocationStrategy serviceBehavior;
  private ResponseStrategy responseBehavior;

  private final Log logger = LogFactory.getLog(this.getClass());

  public AppModelImpl(AuthorizationStrategy authStrategy, ValidationStrategy validationStrategy,
      ServiceInvocationStrategy serviceStrategy, ResponseStrategy responseStrategy) {
    this.authBehavior = authStrategy;
    this.validationBehavior = validationStrategy;
    this.serviceBehavior = serviceStrategy;
    this.responseBehavior = responseStrategy;
  }

  @Override
  public ResponseEntity execute(Map<String, String> input, Map<String, String> header,
      String body) {
    if (!authBehavior.isAuthorized(header)) {
      return responseBehavior.notAuthenticatedResponse(authBehavior.getError());
    }
    if (!validationBehavior.inputIsValid(input, body)) {
      return responseBehavior.invalidInputResponse(validationBehavior.getError());
    }
    Object responseData;
    //Invoke service and catch the different exceptions that might be raised
    try {
      responseData = serviceBehavior.invokeService(input, body, authBehavior.getUserId(header));
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(duplicateKeyError);
    } catch (NonTransientDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(nonTransientExceptionError);
    } catch (RecoverableDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(recoverableExceptionError);
    } catch (ScriptException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(scriptExceptionError);
    } catch (TransientDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(transientExceptionError);
    } catch (ResourceNotFoundException e) {
      e.printStackTrace();
      return responseBehavior.notFound(e.getMessage());
    } catch (WrongPasswordException e) {
      e.printStackTrace();
      return responseBehavior.notAuthenticatedResponse(wrongPasswordError);
    } catch (NotAuthorizedException e) {
      e.printStackTrace();
      return responseBehavior.notAuthorizedResponse(e.getMessage());
    } catch (DBException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(exceptionError);
    }
    return responseBehavior.successResponse(responseData);
  }

  @Override
  public ResponseEntity executeFile(MultipartFile file, Map<String, String> header){
    if (!authBehavior.isAuthorized(header)) {
      return responseBehavior.notAuthenticatedResponse(authBehavior.getError());
    }
    Object responseData;
    //Invoke service and catch the different exceptions that might be raised
    try {
      responseData = serviceBehavior.invokeFileService(file, authBehavior.getUserId(header));
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(duplicateKeyError);
    } catch (NonTransientDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(nonTransientExceptionError);
    } catch (RecoverableDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(recoverableExceptionError);
    } catch (ScriptException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(scriptExceptionError);
    } catch (TransientDataAccessException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(transientExceptionError);
    } catch (ResourceNotFoundException e) {
      e.printStackTrace();
      return responseBehavior.notFound(e.getMessage());
    } catch (WrongPasswordException e) {
      e.printStackTrace();
      return responseBehavior.notAuthenticatedResponse(wrongPasswordError);
    } catch (NotAuthorizedException e) {
      e.printStackTrace();
      return responseBehavior.notAuthorizedResponse(e.getMessage());
    } catch (DBException e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      return responseBehavior.exceptionResponse(exceptionError);
    }
    return responseBehavior.successResponse(responseData);
  }
}
