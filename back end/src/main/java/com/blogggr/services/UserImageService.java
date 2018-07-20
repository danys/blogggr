package com.blogggr.services;

import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserImageDao;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.StorageException;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.ImageScaler;
import com.blogggr.utilities.SimpleBundleMessageSource;
import com.blogggr.utilities.TimeUtilities;
import java.io.IOException;
import javax.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
@Service
@Transactional
public class UserImageService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserImageDao userImageDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  @Qualifier("userimage")
  private FileStorageManager fileStorageManager;

  @Autowired
  private SimpleBundleMessageSource simpleBundleMessageSource;

  private static final int MAX_TRIES = 100;

  private static final String IMG_EXTENSION = ".png";
  private static final String ORIGINAL_IMG_EXTENSION = "_original" + IMG_EXTENSION;
  private static final int IMG_HEIGHT = 128;
  private static final int IMG_WIDTH = 128;

  public UserImage postImage(long userId, MultipartFile file) {
    logger.debug("UserImageService | postImage - userId: {}, file: {}", userId, file);
    User user = userDao.findById(userId);
    if (user == null) {
      throw new IllegalArgumentException(
          simpleBundleMessageSource.getMessage("exception.authentication.userNotFound"));
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
      if (userImageDao.findByName(scaledImageName) == null) {
        ok = true;
      }
    }
    if (tries == MAX_TRIES) {
      throw new IllegalStateException(
          simpleBundleMessageSource.getMessage("UserImageService.postImage.tooManyTries"));
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
      throw new StorageException(
          simpleBundleMessageSource.getMessage("UserImageService.postImage.scalingException"), e);
    }

    //Store image in the cloud
    try {
      fileStorageManager.storeOnCloud(scaledImageName, name);
    } catch (IOException e) {
      throw new StorageException(
          simpleBundleMessageSource.getMessage("UserImageService.postImage.storageError"), e);
    }

    //Remove the the original as well as the scaled image
    try {
      fileStorageManager
          .delete(fileStorageManager.getStorageDirectory().resolve(originalImageName));
    } catch (IOException e) {
      throw new StorageException(
          simpleBundleMessageSource.getMessage("UserImageService.postImage.temporaryFileError"), e);
    }

    //Update isCurrent to false on all user images
    userImageDao.unsetCurrent(user.getUserId());

    //Store image reference in db
    UserImage selectedUserImage = new UserImage();
    selectedUserImage.setName(scaledImageName);
    selectedUserImage.setUser(user);
    selectedUserImage.setWidth(IMG_WIDTH);
    selectedUserImage.setHeight(IMG_HEIGHT);
    selectedUserImage.setIsCurrent(true);
    userImageDao.save(selectedUserImage);
    return selectedUserImage;
  }

  public Resource getUserImage(String fileName) {
    logger.debug("UserImageService | getUserImage - fileName: {}", fileName);
    UserImage userImage = userImageDao.findByName(fileName);
    if (userImage == null) {
      throw new ResourceNotFoundException(
          simpleBundleMessageSource.getMessage("UserImageService.getUserImage.notFoundException"));
    }
    return this.fileStorageManager.getImageResourceFromCloud(fileName);
  }
}
