package com.blogggr.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Sunnen on 27.07.18.
 */
@RestController
public class CustomErrorController implements ErrorController {

  private static final String PATH = "/error";

  @RequestMapping(value = PATH)
  public String error() {
    return "Error path not found!";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }
}
