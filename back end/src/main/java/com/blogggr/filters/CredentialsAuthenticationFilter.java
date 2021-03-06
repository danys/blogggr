package com.blogggr.filters;

import com.blogggr.responses.ResponseBuilder;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Created by Daniel Sunnen on 03.06.18.
 */
public class CredentialsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final String USERNAME_KEY = "email";
  private static final String PASSWORD_KEY = "password";

  private final Logger logging = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  /**
   * POST to /sessions with valid credentials in order to obtain a fresh JWT
   */
  public CredentialsAuthenticationFilter() {
    super(new AntPathRequestMatcher("/api/v*/sessions", "POST"));
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    if (!request.getMethod().equals("POST")) {
      return null;
    }
    BufferedReader br = request.getReader();
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    if (sb.length() == 0){
      logger.debug("No login information provided!");
      return null;
    }
    Map<String, String> parameterMap =
        new ObjectMapper().readValue(sb.toString(), HashMap.class);
    String username = parameterMap.get(USERNAME_KEY);
    String password = parameterMap.get(PASSWORD_KEY);
    if (username == null || password == null) {
      logger.debug("No login information provided!");
      return null;
    }
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        username, password);
    return this.getAuthenticationManager().authenticate(authRequest);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    if (!requiresAuthentication(request, response)) {
      chain.doFilter(request, response);
      return;
    }
    if (SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder
        .getContext()
        .getAuthentication() instanceof AnonymousAuthenticationToken) {
      try {
        Authentication auth = attemptAuthentication(request, response);
        if (auth != null) {
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (BadCredentialsException e) {
        //Log exception, exit the Spring Security filter chain and return
        String message = simpleBundleMessageSource.getMessage("exception.db.wrongPasswordError");
        logging.error(message);
        ResponseBuilder.filterResponse(response, message, HttpStatus.UNAUTHORIZED);
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
