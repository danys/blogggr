package com.blogggr.responses;

import lombok.Getter;

/**
 * Created by Daniel Sunnen on 06.06.18.
 */
@Getter
public class SuccessResponse {

  private String apiVersion;
  private Object data;

  public SuccessResponse(String apiVersion, Object data){
    this.apiVersion = apiVersion;
    this.data = data;
  }
}
