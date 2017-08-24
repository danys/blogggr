package com.blogggr.services;

import com.blogggr.dao.UserImageDAO;
import com.blogggr.entities.UserImage;
import com.blogggr.utilities.FileStorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
public class UserImageServiceImpl implements UserImageService {

  private UserImageDAO userImageDAO;

  private FileStorageManager fileStorageManager;

  @Autowired
  public UserImageServiceImpl(UserImageDAO userImageDAO,
      @Qualifier("userimage") FileStorageManager fileStorageManager) {
    this.userImageDAO = userImageDAO;
    this.fileStorageManager = fileStorageManager;
  }

  @Override
  public UserImage postImage(long userId, MultipartFile file) {
    //Generate img name
    //Write image to disk
    fileStorageManager.store(file, file.getName());
    //Scale image
    UserImage userImage = new UserImage();
    //TODO set file details
    userImageDAO.save(userImage);
    return userImage;
  }
}
