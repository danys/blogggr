package com.blogggr.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.services.UserService;
import com.blogggr.utilities.JwtHelper;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Created by Daniel Sunnen on 03.06.18.
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

  public static final String JWT_TOKEN_KEY = "Authorization";

  private final Logger logging = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @Autowired
  private UserService userService;

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String token = request.getHeader(JWT_TOKEN_KEY);
    String username;
    try {
      if (token != null) {
        username = JwtHelper.getSubjectFromValidJwt(token);
        UserDetails loadedUser = userService.loadUserByUsername(username);
        if (loadedUser != null) {
          UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(
              loadedUser, token, new ArrayList<>());
          SecurityContextHolder.getContext().setAuthentication(authResult);
        }
      }
    } catch (TokenExpiredException e) {
      String message = simpleBundleMessageSource.getMessage("exception.authentication.jwtExpired");
      logging.error(message, e);
      ResponseBuilder.filterResponse(response, message, HttpStatus.UNAUTHORIZED);
      return;
    } catch(Exception e){
      String message = simpleBundleMessageSource.getMessage("exception.authentication.jwtError");
      logging.error(message, e);
      ResponseBuilder.filterResponse(response, message, HttpStatus.UNAUTHORIZED);
      return;
    }
    chain.doFilter(request, response);
  }
}
