package com.blogggr.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Daniel Sunnen on 29.05.18.
 */
@Controller
public class RedirectController {

  @GetMapping(value = "/signup")
  public String signUpPage(HttpServletRequest request, HttpServletResponse response) {
    return "forward:/index.html";
  }

  @GetMapping(value = "/login")
  public String loginPage(HttpServletRequest request, HttpServletResponse response) {
    return "forward:/index.html";
  }
}