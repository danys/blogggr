package com.blogggr.responses;

import java.util.List;
import lombok.Getter;

/**
 * Created by Daniel Sunnen on 11.06.18.
 */
@Getter
public class ErrorResponseBody {

  private String apiVersion;
  private List<String> errors;

  public ErrorResponseBody(String apiVersion, List<String> errors){
    this.apiVersion = apiVersion;
    this.errors = errors;
  }
}
