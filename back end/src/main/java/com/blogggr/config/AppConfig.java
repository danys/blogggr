package com.blogggr.config;

import com.blogggr.utilities.FileStorageManager;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
  private static final String urlPrefix = "/api/v";
  public static final String baseUrl = urlPrefix + apiVersion;

  private static Integer port;
  public static String hostUrl;
  public static String fullBaseUrl = hostUrl + baseUrl;

  public static final String locationHeaderKey = "Location";
  public static final String authKey = "Auth";
  public static final String validityUntilKey = "ValidUntil";
  public static final String emailKey = "email";
  public static final long sessionValidityMillis = 1000 * 60 * 60
      * 24; //one day: maximum validity of a session. Max also applies for extensions.
  public static final String headerAuthorizationKey = "authorization";
  public static final int maxPostBodyLength = 100;
  public static final String validEmailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

  public static final ZoneId luxembourgZoneId = ZoneId.of("Europe/Luxembourg");

  @Value("${imgapikey}")
  private String imageApiKey;

  @Value("${imgapisecret}")
  private String imageApiSecret;

  @Autowired
  private StorageConfig storageConfig;

  @Value("${server.port:8080}")
  public void setPort(Integer configPort) {
    AppConfig.port = configPort;
    AppConfig.hostUrl = "http://localhost" + String.valueOf(AppConfig.port);
    AppConfig.fullBaseUrl = AppConfig.hostUrl + AppConfig.baseUrl;
  }

  @Bean
  @Qualifier("userimage")
  public FileStorageManager userImageFileStorageManager() {
    return new FileStorageManager(storageConfig.getUserImagesLocation(), imageApiKey, imageApiSecret);
  }

  @Bean
  @Qualifier("postimage")
  public FileStorageManager postImageFileStorageManager() {
    return new FileStorageManager(storageConfig.getPostImagesLocation(), imageApiKey, imageApiSecret);
  }
}
