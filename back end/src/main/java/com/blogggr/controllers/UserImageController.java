package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.exceptions.StorageException;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.UserImageService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokePostUserImageService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.utilities.Cryptography;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
  private UserService userService;

  @Autowired
  private Cryptography cryptography;

  @Autowired
  private UserImageService userImageService;

  //POST /userimages
  @RequestMapping(path = USER_IMAGE_PATH, method = RequestMethod.POST)
  public ResponseEntity postUserImage(@RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> header, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info(
        "[POST /userimages] Header: {}", header);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        null, new InvokePostUserImageService(userImageService), new PostResponse());
    return model.executeFile(file, header);
  }

  //GET /userimages/filename
  @GetMapping("/userimages/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
    logger.info(
        "[GET /userimages/{}]", fileName);
    try {
      Resource file = userImageService.getFileStorageManager().getImageResourceFromCloud(fileName);
      return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    } catch(StorageException e){
      return null;
    }
  }
}
