package com.blogggr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
@ConfigurationProperties("storage")
@Getter
@Setter
public class StorageConfig {

  private String userImagesLocation = "userimages";

  private String postImagesLocation = "postimages";
}
