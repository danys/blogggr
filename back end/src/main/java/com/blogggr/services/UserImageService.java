package com.blogggr.services;

import com.blogggr.entities.UserImage;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
public interface UserImageService {

  UserImage postImage(long userId, MultipartFile file);

}