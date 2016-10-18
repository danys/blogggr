package com.blogggr.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Sunnen on 17.10.16.
 */
@RestController
public class DefaultController {

    @RequestMapping("/hello")
    public String hello() {
        return new String("Hello world");
    }
}
