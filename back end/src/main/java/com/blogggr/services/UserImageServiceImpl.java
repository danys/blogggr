package com.blogggr.services;

import com.blogggr.dao.UserDAO;
import com.blogggr.dao.UserImageDAO;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.TimeUtilities;
import javax.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserImageServiceImpl implements UserImageService {

  private UserImageDAO userImageDAO;
  private UserDAO userDAO;

  private FileStorageManager fileStorageManager;

  private static final int MAX_TRIES = 100;

  @Autowired
  public UserImageServiceImpl(UserImageDAO userImageDAO,
      UserDAO userDAO,
      @Qualifier("userimage") FileStorageManager fileStorageManager) {
    this.userImageDAO = userImageDAO;
    this.fileStorageManager = fileStorageManager;
    this.userDAO = userDAO;
  }

  @Override
  public UserImage postImage(long userId, MultipartFile file) {
    User user = userDAO.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found!");
    }
    UserImage userImage = new UserImage();
    //Generate img name: 64 char sequence
    String name = "";
    boolean ok = false;
    int tries = 0;
    while (!ok && tries < MAX_TRIES) {
      name = Cryptography
          .computeSHA256Hash(String.valueOf(TimeUtilities.getCurrentTimestamp().getTime()))
          .substring(0, 64);
      tries++;
      try {
        userImageDAO.findByName(name);
      } catch (NoResultException e) {
        ok = true;
      }
    }
    if (tries == MAX_TRIES) {
      throw new IllegalStateException("Too many tries!");
    }
    userImage.setName(name);
    userImage.setUser(user);
    //Set dummy date that will be updated later (null constraint)
    userImage.setWidth(0);
    userImage.setHeight(0);
    userImageDAO.save(userImage);

    //Write image to disk
    fileStorageManager.store(file, file.getName());
    //Scale image

    //TODO set file details

    return userImage;
  }
}
