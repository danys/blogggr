package com.blogggr.services;

import com.blogggr.dao.UserDAO;
import com.blogggr.dao.UserImageDAO;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.exceptions.StorageException;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.ImageScaler;
import com.blogggr.utilities.ImageScaler.ImageSize;
import com.blogggr.utilities.TimeUtilities;
import java.io.IOException;
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

  private final String IMG_EXTENSION = ".png";
  private final String ORIGINAL_IMG_EXTENSION = "_original" + IMG_EXTENSION;
  private final int IMG_HEIGHT = 128;
  private final int IMG_WIDTH = 128;

  @Autowired
  public UserImageServiceImpl(UserImageDAO userImageDAO,
      UserDAO userDAO,
      @Qualifier("userimage") FileStorageManager fileStorageManager) {
    this.userImageDAO = userImageDAO;
    this.fileStorageManager = fileStorageManager;
    this.userDAO = userDAO;
  }

  @Override
  public UserImage postImage(long userId, MultipartFile file) throws StorageException {
    User user = userDAO.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found!");
    }
    //Generate img name: 64 char sequence
    String name = "";
    String scaledImageName;
    boolean ok = false;
    int tries = 0;
    while (!ok && tries < MAX_TRIES) {
      name = Cryptography
          .computeSHA256Hash(String.valueOf(TimeUtilities.getCurrentTimestamp().getTime()))
          .substring(0, 51);
      scaledImageName = name + IMG_EXTENSION;
      tries++;
      try {
        userImageDAO.findByName(scaledImageName);
      } catch (NoResultException e) {
        ok = true;
      }
    }
    if (tries == MAX_TRIES) {
      throw new IllegalStateException("Too many tries!");
    }
    String originalImageName = name + ORIGINAL_IMG_EXTENSION;
    scaledImageName = name + IMG_EXTENSION;

    //Write original image to disk and store correct image size in the db
    fileStorageManager.store(file, originalImageName);

    //Scale image
    try {
      ImageScaler.scaleImageFile(
          fileStorageManager.getStorageDirectory().resolve(originalImageName),
          fileStorageManager.getStorageDirectory().resolve(scaledImageName),
          IMG_WIDTH, IMG_HEIGHT);
    } catch (IOException e) {
      throw new StorageException("Scaling image file!", e);
    }

    //Store image in the cloud
    try {
      fileStorageManager.storeOnCloud(scaledImageName, name);
    } catch (IOException e) {
      throw new StorageException("Error storing image in the cloud!", e);
    }

    UserImage selectedUserImage = new UserImage();
    selectedUserImage.setName(scaledImageName);
    selectedUserImage.setUser(user);
    selectedUserImage.setWidth(IMG_WIDTH);
    selectedUserImage.setHeight(IMG_HEIGHT);
    selectedUserImage.setCurrent(true);
    userImageDAO.save(selectedUserImage);
    return selectedUserImage;
  }

  public FileStorageManager getFileStorageManager() {
    return this.fileStorageManager;
  }
}
