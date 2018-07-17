package com.blogggr.exceptions;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Created by Daniel Sunnen on 17.06.18.
 */
public class CustomizedAuthenticationEntryPoint implements AuthenticationEntryPoint {

  public final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException{
    String message = simpleBundleMessageSource.getMessage("exception.authentication.accessDenied");
    logger.warn(message);
    ResponseBuilder.filterResponse(response, message, HttpStatus.UNAUTHORIZED);
  }
}
