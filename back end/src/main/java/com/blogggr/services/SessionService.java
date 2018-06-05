package com.blogggr.services;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserRepository;
import com.blogggr.entities.User;
import com.blogggr.utilities.JwtHelper;
import java.security.Principal;
import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SessionService {

  public static class SessionDetails {

    public String jwt;
    public ZonedDateTime expiration;
    public String email;
  }

  private final Log logger = LogFactory.getLog(this.getClass());

  public SessionDetails createSession(User user)
      throws UnsupportedEncodingException {
    SessionDetails details = new SessionDetails();
    details.jwt = JwtHelper.generateJwt(user.getEmail());
    try {
      details.expiration = JwtHelper.getExpirationFromValidJwt(details.jwt);
    } catch(Exception e){
      logger.error("Error determining expiration", e);
      details.expiration = ZonedDateTime.now(AppConfig.luxembourgZoneId);
    }
    details.email = user.getEmail();
    return details;
  }
}
