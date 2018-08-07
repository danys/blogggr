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
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Override
  public UserDetails loadUserByUsername(String username) {
    logger.debug("UserService | loadUserByUsername - username: {}", username);
    User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(
          username + " " + simpleBundleMessageSource.getMessage("UserService.userNotFoundShort"));
    }
    return new UserPrincipal(user);
  }

  public User getUserById(long id) {
    logger.debug("UserService | getUserById - id: {}", id);
    return userDao.findById(id);
  }

  public User getUserByIdWithImages(long id) {
    logger.debug("UserService | getUserByIdWithImages - id: {}", id);
    return userDao.findByIdWithImages(id);
  }

  public User getUserByEmail(String email) {
    logger.debug("UserService | getUserByEmail - email: {}", email);
    return userRepository.findByEmail(email);
  }

  //For POST request
  public User createUser(UserPostData userData) {
    logger.debug("UserService | createUser - userData: {}", userData);
    if (userData.getEmail().compareTo(userData.getEmailRepeat()) != 0) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("UserService.createUser.emailMismatch"));
    }
    if (userData.getPassword().compareTo(userData.getPasswordRepeat()) != 0) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("UserService.createUser.passwordMismatch"));
    }
    User user = new User();
    user.setFirstName(userData.getFirstName());
    user.setLastName(userData.getLastName());
    user.setEmail(userData.getEmail());
    user.setSex(userData.getSex());
    user.setLang(userData.getLang());
    user.setPasswordHash(passwordEncoder.encode(userData.getPassword()));
    //Compute a 64 character challenge
    String challenge = Cryptography
        .computeSHA256Hash(
            user.getEmail() + String.valueOf(System.currentTimeMillis() / 10)); //UTC time
    user.setChallenge(challenge);
    Timestamp currentTimestamp = TimeUtilities.getCurrentTimestamp();
    user.setLastChange(currentTimestamp);
    user.setStatus(0);
    return userRepository.save(user);
  }

  public void updateUser(long userResourceId, long userId, UserPutData userData) {
    logger.debug("UserService | updateUser - userResourceID: {}, userId: {}, userData: {}",
        userResourceId, userId, userData);
    User user = userDao.findById(userResourceId);
    if (user == null) {
      throw new ResourceNotFoundException(
          simpleBundleMessageSource.getMessage("UserService.userNotFound"));
    }
    //A user can only change his own data
    if (user.getUserId() != userId) {
      throw new NotAuthorizedException(
          simpleBundleMessageSource.getMessage("UserService.updateUser.notAuthorizedModify"));
    }
    //If an old password has been provided check it!
    if (userData.getOldPassword() != null) {
      String oldHash = passwordEncoder.encode(userData.getOldPassword());
      if (oldHash.compareTo(user.getPasswordHash()) != 0) {
        throw new NotAuthorizedException(
            simpleBundleMessageSource.getMessage("UserService.updateUser.wrongOldPassword"));
      }
    }
    if (userData.getPassword() != null) {
      if (userData.getOldPassword() == null) {
        throw new NotAuthorizedException(
            simpleBundleMessageSource.getMessage("UserService.updateUser.oldPasswordEmpty"));
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
    logger.debug("UserService | getUsers - searchData: {}", searchData);
    return userDao
        .getUsers(searchData.getSearchString(), searchData.getLimit(), searchData.getPageNumber());
  }

  public PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) {
    logger.debug("UserService | getUsersBySearchTerms - searchData: {}", searchData);
    return userDao.getUsersBySearchTerms(searchData);
  }

  public String confirmEmail(Long userId, String challenge) {
    logger.debug("UserService | confirmEmail - userID: {}, challenge: {}", userId, challenge);
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("UserService.userNotFound"));
    }
    User user = userOptional.get();
    if (!user.getChallenge().equals(challenge)) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("UserService.confirmEmail.wrongChallenge"));
    }
    user.setStatus(1);
    return user.getEmail();
  }
}
