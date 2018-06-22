package com.blogggr.services;

import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserRepository;
import com.blogggr.dto.SimpleUserSearchData;
import com.blogggr.entities.User;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import com.blogggr.dto.UserPostData;
import com.blogggr.dto.UserPutData;
import com.blogggr.dto.UserSearchData;
import com.blogggr.security.UserPrincipal;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.TimeUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  @Autowired
  private EmailService emailService;

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return new UserPrincipal(user);
  }

  public User getUserById(long id) {
    return userDao.findById(id);
  }

  public User getUserByIdWithImages(long id) {
    return userDao.findByIdWithImages(id);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  //For POST request
  public User createUser(UserPostData userData) throws DataAccessException {
    if (userData.getEmail().compareTo(userData.getEmailRepeat()) != 0){
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("service.user.createUser.emailMismatch"));
    }
    if (userData.getPassword().compareTo(userData.getPasswordRepeat()) != 0) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("service.user.createUser.passwordMismatch"));
    }
    User user = new User();
    user.setFirstName(userData.getFirstName());
    user.setLastName(userData.getLastName());
    user.setEmail(userData.getEmail());
    user.setSex(Integer.parseInt(userData.getSex()));
    user.setLang(userData.getLang());
    user.setPasswordHash(passwordEncoder.encode(userData.getPassword()));
    //Compute a 64 character challenge
    String challenge = Cryptography
        .computeSHA256Hash(String.valueOf(System.currentTimeMillis() / 10)); //UTC time
    user.setChallenge(challenge);
    Timestamp currentTimestamp = TimeUtilities.getCurrentTimestamp();
    user.setLastChange(currentTimestamp);
    user.setStatus(0);
    //TODO send email with localized text
    //emailService.sendSimpleMessage(userData.getEmail(),"bloggr.com - Validate your registration","");
    return userRepository.save(user);
  }

  public void updateUser(long userResourceID, long userID, UserPutData userData)
      throws ResourceNotFoundException, NotAuthorizedException {
    User user = userDao.findById(userResourceID);
    if (user == null) {
      throw new ResourceNotFoundException("User not found!");
    }
    //A user can only change his own data
    if (user.getUserId() != userID) {
      throw new NotAuthorizedException("Not authorized to change this user!");
    }
    //If an old password has been provided check it!
    if (userData.getOldPassword() != null) {
      String oldHash = passwordEncoder.encode(userData.getOldPassword());
      if (oldHash.compareTo(user.getPasswordHash()) != 0) {
        throw new NotAuthorizedException("Old password is wrong!");
      }
    }
    if (userData.getPassword() != null) {
      if (userData.getOldPassword() == null) {
        throw new NotAuthorizedException("Old password must be provided!");
      }
      user.setPasswordHash(passwordEncoder.encode(userData.getPassword()));
    }
    if (userData.getEmail() != null) {
      user.setEmail(userData.getEmail());
    }
    if (userData.getLastName() != null) {
      user.setLastName(userData.getLastName());
    }
    if (userData.getFirstName() != null) {
      user.setFirstName(userData.getFirstName());
    }
    user.setLastChange(TimeUtilities.getCurrentTimestamp());
    userDao.save(user);
  }

  public RandomAccessListPage<User> getUsers(SimpleUserSearchData searchData) {
    RandomAccessListPage<User> usersPage = userDao
        .getUsers(searchData.getSearchString(), searchData.getLimit(), searchData.getPageNumber());
    return usersPage;
  }

  public PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) {
    return userDao.getUsersBySearchTerms(searchData);
  }
}
