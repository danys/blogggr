package com.blogggr.responses;

import lombok.Getter;

/**
 * Created by Daniel Sunnen on 06.06.18.
 */
@Getter
public class DataResponseBody {

  private String apiVersion;
  private Object data;

  public DataResponseBody(String apiVersion, Object data){
    this.apiVersion = apiVersion;
    this.data = data;
  }
}
