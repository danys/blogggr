package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.UserImage;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.StorageException;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.UserImageService;
import com.blogggr.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 22.08.17.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class UserImageController {

  private static final String USER_IMAGE_PATH = "/userimages";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserImageService userImageService;

  /**
   * POST /userimages
   *
   * @param file the image that is uploaded
   * @param userPrincipal the logged in user
   */
  @RequestMapping(path = USER_IMAGE_PATH, method = RequestMethod.POST)
  public ResponseEntity postUserImage(@RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws StorageException {
    logger.info(
        "[POST /userimages] User: {}", userPrincipal.getUser().getEmail());
    UserImage userImage = userImageService.postImage(userPrincipal.getUser().getUserId(), file);
    return ResponseBuilder.postSuccessResponse(
        AppConfig.fullBaseUrl + USER_IMAGE_PATH + '/' + userImage.getName());
  }

  /**
   * GET /userimages/filename
   * @param fileName the filename of the file to retrieve
   * @param userPrincipal the logged in user
   * @return
   * @throws StorageException
   * @throws ResourceNotFoundException
   */
  @GetMapping("/userimages/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> getUserImage(@PathVariable String fileName,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws StorageException, ResourceNotFoundException {
    logger.info(
        "[GET /userimages/{}. User: {}]", fileName, userPrincipal.getUser().getEmail());

    Resource file = userImageService.getUserImage(fileName);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);

  }
}
