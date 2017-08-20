package com.blogggr.services;

import com.blogggr.dao.UserDAO;
import com.blogggr.entities.User;
import com.blogggr.exceptions.*;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.utilities.Cryptography;
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
public class SessionServiceImpl implements SessionService {

  public static class SessionDetails {

    public String jwt;
    public java.util.Date expiration;
    public String email;
  }

  private final Log logger = LogFactory.getLog(this.getClass());
  private UserDAO userDAO;
  private Cryptography cryptography;

  public SessionServiceImpl(UserDAO userDAO, Cryptography cryptography) {
    this.userDAO = userDAO;
    this.cryptography = cryptography;
  }

  @Override
  public SessionDetails createSession(SessionPostData sessionData)
      throws ResourceNotFoundException, DBException, WrongPasswordException, UnsupportedEncodingException {
    User user = userDAO.getUserByEmail(sessionData.getEmail());
    //Check that the supplied password is correct
    String storedPasswordHash = user.getPasswordHash();
    String storedSalt = user.getSalt();
    String submitPasswordHash = Cryptography
        .computeSHA256Hash(sessionData.getPassword() + storedSalt);
    if (submitPasswordHash.compareTo(storedPasswordHash) != 0) {
      throw new WrongPasswordException("Supplied password is wrong!");
    }
    SessionDetails details = new SessionDetails();
    details.jwt = cryptography.generateJWT(user.getEmail());
    details.expiration = cryptography.getExpirationFromValidJWT(details.jwt);
    details.email = sessionData.getEmail();
    return details;
  }
}
