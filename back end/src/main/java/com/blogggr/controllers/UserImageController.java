package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokePostUserImageService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.utilities.Cryptography;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

  private UserService userService;
  private Cryptography cryptography;

  public UserImageController(UserService userService, Cryptography cryptography) {
    this.userService = userService;
    this.cryptography = cryptography;
  }

  //POST /userimages
  @RequestMapping(path = USER_IMAGE_PATH, method = RequestMethod.POST)
  public ResponseEntity postUserImage(@RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> header) {
    logger.debug(
        "[POST /userimages] Header: {}", header);
    AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography),
        null, new InvokePostUserImageService(), new PostResponse());
    return model.executeFile(file, header);
  }
}
