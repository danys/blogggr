package com.blogggr.requestdata;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 01.08.17.
 */
@Getter
@Setter
public class PrevNextData<T> {

  @NotNull
  private Integer maxRecordsCount;

  private T before;

  private T after;
}
