package com.blogggr.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Sunnen on 27.07.18.
 */
@RestController
public class CustomErrorController implements ErrorController {

  @RequestMapping(value = "/error")
  public String error() {
    return "Error path not found!";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
