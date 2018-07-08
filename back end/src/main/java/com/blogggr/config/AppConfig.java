package com.blogggr.config;

import com.blogggr.utilities.DtoConverter;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.time.ZoneId;
import java.util.Arrays;
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

  private static final String urlPrefix = "/api/v";
  public static final String apiVersion = "1.0";
  public static final String baseUrl = urlPrefix + apiVersion;

  public static String domain = "https://www.blogggr.com";
  public static String fullBaseUrl = domain + '/' + baseUrl;

  public static final String locationHeaderKey = "Location";

  public static final int maxPostBodyLength = 100;

  public static final ZoneId luxembourgZoneId = ZoneId.of("Europe/Luxembourg");

  public static final List<String> languages = Arrays.asList("en", "de", "fr");

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

  @Autowired
  private StorageConfig storageConfig;

  @Bean
  @Qualifier("userimage")
  public FileStorageManager userImageFileStorageManager(SimpleBundleMessageSource messageSource) {
    return new FileStorageManager(storageConfig.getUserImagesLocation(), imageApiKey,
        imageApiSecret, messageSource);
  }

  @Bean
  @Qualifier("postimage")
  public FileStorageManager postImageFileStorageManager(SimpleBundleMessageSource messageSource) {
    return new FileStorageManager(storageConfig.getPostImagesLocation(), imageApiKey,
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
