package com.blogggr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Daniel Sunnen on 17.10.16.
 */
@Configuration
@EnableTransactionManagement
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
}
