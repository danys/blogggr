package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 08.06.18.
 */
@Getter
@Setter
public class SimpleUserSearchData {

  @NotNull
  private String searchString;

  @NotNull
  private Integer limit;

  @NotNull
  private Integer pageNumber;
}