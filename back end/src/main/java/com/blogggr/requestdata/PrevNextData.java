package com.blogggr.requestdata;

import javax.validation.constraints.NotNull;

/**
 * Created by Daniel Sunnen on 01.08.17.
 */
public class PrevNextData<T> {

  @NotNull
  private Integer length;

  private T before;

  private T after;

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public T getBefore() {
    return before;
  }

  public void setBefore(T before) {
    this.before = before;
  }

  public T getAfter() {
    return after;
  }

  public void setAfter(T after) {
    this.after = after;
  }
}
