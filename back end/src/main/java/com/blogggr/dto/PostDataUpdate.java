package com.blogggr.dto;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 23.06.18.
 */
@Getter
@Setter
public class PostDataUpdate {

  @Size(min=4)
  private String title;

  @Size(min=10)
  private String textBody;

  private Boolean isGlobal;
}