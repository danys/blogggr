package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 08.06.18.
 */
@Getter
@Setter
public class SimpleUserSearchData {

  @NotNull
  @Size(min = 3)
  private String searchString;

  @NotNull
  private Integer limit;

  private Integer pageNumber;
}