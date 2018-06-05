package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.SessionService;
import com.blogggr.services.SessionService.SessionDetails;
import com.blogggr.utilities.Cryptography;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class SessionsController {

  public static final String sessionPath = "/sessions";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SessionService sessionService;

  /**
   * POST /sessions <br/>
   * Can be used to get an initial JWT token using a username and a password <br/>
   * or a new fresh token using an earlier JWT that is still valid (see CredentialsAuthenticationFilter class).
   * @param userPrincipal the authenticated user
   * @return an object containing a new JWT, an expiration time and a email address of the principal
   * @throws UnsupportedEncodingException
   */
  @RequestMapping(path = sessionPath, method = RequestMethod.POST)
  public SessionDetails createSession(@AuthenticationPrincipal UserPrincipal userPrincipal) throws UnsupportedEncodingException{
    logger.info("[POST /sessions] User: {}", userPrincipal.getUser().getEmail());
    return sessionService.createSession(userPrincipal.getUser());
  }
}
