package com.blogggr;

import com.blogggr.config.StorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@EnableConfigurationProperties(StorageConfig.class)
public class Application {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Application.class, args);
    DispatcherServlet dispatcherServlet = (DispatcherServlet)ctx.getBean("dispatcherServlet");
    dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
  }
}
