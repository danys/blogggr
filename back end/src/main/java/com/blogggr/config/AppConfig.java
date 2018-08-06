package com.blogggr.config;

import com.blogggr.utilities.DtoConverter;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Created by Daniel Sunnen on 17.10.16.
 */
@Configuration
public class AppConfig {

  private static final String URL_PREFIX = "/api/v";
  public static final String API_VERSION = "1.0";
  public static final String BASE_URL = URL_PREFIX + API_VERSION;

  public static final String DOMAIN = "https://www.blogggr.com";
  public static final String FULL_BASE_URL = DOMAIN + '/' + BASE_URL;

  public static final String LOCATION_HEADER_KEY = "Location";

  public static final int MAX_POST_BODY_LENGTH = 100;

  public static final ZoneId LUXEMBOURG_ZONE_ID = ZoneId.of("Europe/Luxembourg");

  public static final List<String> LANGUAGES = Collections
      .unmodifiableList(Arrays.asList("en", "de", "fr"));

  //Image storage
  @Value("${imgapi.key}")
  private String imageApiKey;

  @Value("${imgapi.secret}")
  private String imageApiSecret;

  //SMTP server config
  @Value("${smtp.host}")
  private String smtpHost;

  @Value("${smtp.port}")
  private String smtpPort;

  @Value("${smtp.username}")
  private String smtpUsername;

  @Value("${smtp.password}")
  private String smtpPassword;

  private static final String USER_IMAGES_LOCATION = "userimages";
  private static final String POST_IMAGES_LOCATION = "postimages";

  @Bean
  @Qualifier("userimage")
  public FileStorageManager userImageFileStorageManager(SimpleBundleMessageSource messageSource) {
    return new FileStorageManager(USER_IMAGES_LOCATION, imageApiKey,
        imageApiSecret, messageSource);
  }

  @Bean
  @Qualifier("postimage")
  public FileStorageManager postImageFileStorageManager(SimpleBundleMessageSource messageSource) {
    return new FileStorageManager(POST_IMAGES_LOCATION, imageApiKey,
        imageApiSecret, messageSource);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public DtoConverter dtoConverter() {
    return new DtoConverter(modelMapper());
  }

  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.ENGLISH);
    return slr;
  }

  @Bean
  public SimpleBundleMessageSource messageSource() {
    SimpleBundleMessageSource messageSource = new SimpleBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(smtpHost);
    mailSender.setPort(Integer.parseInt(smtpPort));
    mailSender.setUsername(smtpUsername);
    mailSender.setPassword(smtpPassword);
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
    return mailSender;
  }
}
