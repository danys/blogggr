package com.blogggr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
@ConfigurationProperties("storage")
public class StorageConfig {

  private String userImagesLocation = "/var/blogggrimages/userimages";

  private String postImagesLocation = "/var/blogggrimages/postimages";

  public String getUserImagesLocation() {
    return userImagesLocation;
  }

  public void setUserImagesLocation(String userImagesLocation) {
    this.userImagesLocation = userImagesLocation;
  }

  public String getPostImagesLocation() {
    return postImagesLocation;
  }

  public void setPostImagesLocation(String postImagesLocation) {
    this.postImagesLocation = postImagesLocation;
  }
}
