package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.SessionService;
import com.blogggr.services.SessionService.SessionDetails;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@RestController
@RequestMapping(AppConfig.BASE_URL)
public class SessionsController {

  public static final String SESSIONS_PATH = "/sessions";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SessionService sessionService;

  @Autowired
  private SimpleBundleMessageSource messageSource;

  /**
   * POST /sessions <br/>
   * Can be used to get an initial JWT token using a username and a password <br/>
   * or a new fresh token using an earlier JWT that is still valid (see CredentialsAuthenticationFilter class).
   * @param userPrincipal the authenticated user
   * @return an object containing a new JWT, an expiration time and a email address of the principal
   * @throws UnsupportedEncodingException
   */
  @PostMapping(value = SESSIONS_PATH)
  public ResponseEntity createSession(@AuthenticationPrincipal UserPrincipal userPrincipal) throws UnsupportedEncodingException{
    logger.info("[POST /sessions] User: {}", userPrincipal.getUser().getEmail());
    if (userPrincipal.getUser().getStatus() == 0) {
      return ResponseBuilder.errorResponse(messageSource.getMessage("SessionService.userDisabledError"), HttpStatus.FORBIDDEN);
    }
    SessionDetails session = sessionService.createSession(userPrincipal.getUser());
    return ResponseBuilder.postSuccessResponseWithData("null", session);
  }
}
