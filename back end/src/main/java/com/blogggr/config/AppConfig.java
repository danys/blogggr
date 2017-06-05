package com.blogggr.config;

import com.blogggr.filters.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.Filter;

/**
 * Created by Daniel Sunnen on 17.10.16.
 */
@Configuration
public class AppConfig {

    public static final String apiVersion = "1.0";
    private  static final String urlPrefix = "/api/v";

    public static final String baseUrl = urlPrefix+apiVersion;
    public static final String hostUrl = "http://localhost:8080";
    public static final String fullBaseUrl = hostUrl+baseUrl;
    public static final String locationHeaderKey = "Location";
    public static final String authKey = "Auth";
    public static final String validityUntilKey = "ValidUntil";
    public static final long sessionValidityMillis = 1000*60*60*24; //one day: maximum validity of a session. Max also applies for extensions.
    public static final String headerAuthorizationKey = "authorization";
    public static final int maxPostBodyLength = 100;

    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(xssFilter());
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setName("xssFilter");
        filterRegBean.setOrder(1);
        return filterRegBean;
    }

    public Filter xssFilter(){
        return new XSSFilter();
    }
}
