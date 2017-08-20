package com.blogggr.filters;

import javax.servlet.FilterConfig;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Daniel Sunnen on 05.06.17.
 */
public class XSSFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //do nothing
  }

  @Override
  public void destroy() {
    //nothing to destroy
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
  }
}
