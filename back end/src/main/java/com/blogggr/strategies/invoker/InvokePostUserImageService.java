package com.blogggr.strategies.invoker;

import com.blogggr.entities.UserImage;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.StorageException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.services.UserImageService;
import com.blogggr.strategies.FileServiceInvocation;
import java.io.UnsupportedEncodingException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 23.08.17.
 */
public class InvokePostUserImageService extends FileServiceInvocation {

  private UserImageService userImageService;

  public InvokePostUserImageService(UserImageService userImageService) {
    this.userImageService = userImageService;
  }

  @Override
  public Object invokeFileService(MultipartFile file, Long userID)
      throws DBException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException, StorageException {
    UserImage userImage = userImageService.postImage(userID, file);
    return null;
  }
}
