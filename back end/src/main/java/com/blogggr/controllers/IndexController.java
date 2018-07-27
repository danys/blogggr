package com.blogggr.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Daniel Sunnen on 27.07.18.
 */
@Controller
public class IndexController {

  @RequestMapping("/{path:[^\\.]+}")
  public String forward() {
    return "forward:/index.html";
  }
}
