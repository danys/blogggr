package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@Getter
@Setter
public class PostData {

  @NotNull
  @Size(min=4)
  private String title;

  @NotNull
  @Size(min=10)
  private String textBody;

  @NotNull
  private Boolean isGlobal;
}
