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

  @Autowired
  private Cryptography cryptography;

  //POST /sessions
  @RequestMapping(path = sessionPath, method = RequestMethod.POST)
  public SessionDetails createSession(@AuthenticationPrincipal UserPrincipal userPrincipal) throws UnsupportedEncodingException{
    logger.info("[POST /sessions]");
    return sessionService.createSession(userPrincipal.getUser());
  }
}
