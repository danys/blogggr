package com.blogggr.strategies.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.blogggr.config.AppConfig;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.services.UserService;
import com.blogggr.strategies.AuthorizationStrategy;
import com.blogggr.utilities.Cryptography;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 07.11.16. Only extracts the userID from the current session
 */
public class AuthenticatedAuthorization implements AuthorizationStrategy {

  private UserService userService;
  private User authenticatedUser;
  private boolean dbCheck;
  private Cryptography cryptography;

  private String errorMessage;

  public static final String sessionExpiredText = "Session is expired!";
  public static final String notAuthenticatedText = "Not authenticated!";

  public AuthenticatedAuthorization(UserService userService, Cryptography cryptography) {
    this.userService = userService;
    this.cryptography = cryptography;
    dbCheck = false;
    errorMessage = "";
  }

  @Override
  public boolean isAuthorized(Map<String, String> header) {
    //Extract the Authorization value
    if (!header.containsKey(AppConfig.headerAuthorizationKey)) {
      errorMessage = notAuthenticatedText;
      return false;
    }
    String authHash = header.get(AppConfig.headerAuthorizationKey);
    //Decode the session hash
    User user;
    try {
      dbCheck = true; //executed DB call to find the current user
      user = userService.getUserByEmail(cryptography.getSubjectFromValidJWT(authHash));
      authenticatedUser = user;
      return true;
    } catch (ResourceNotFoundException e) {
      errorMessage = e.getMessage();
      return false;
    } catch (UnsupportedEncodingException e) {
      //Actually an internal server error
      errorMessage = e.getMessage();
      return false;
    } catch (JWTVerificationException e) {
      errorMessage = sessionExpiredText;
      return false;
    } catch (DBException e) {
      errorMessage = e.getMessage();
      return false;
    }
  }

  @Override
  public Long getUserId(Map<String, String> header) {
    if (dbCheck) {
      if (authenticatedUser == null) {
        return null;
      } else {
        return authenticatedUser.getUserId();
      }
    }
    //The DB has not yet been queried, do it now
    isAuthorized(header);
    if (authenticatedUser == null) {
      return null;
    }
    return authenticatedUser.getUserId();
  }

  @Override
  public String getError() {
    return errorMessage;
  }
}
