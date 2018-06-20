package com.blogggr.responses;

import com.blogggr.responses.ResponseBuilder.FieldError;
import java.util.List;
import lombok.Getter;

/**
 * Created by Daniel Sunnen on 19.06.18.
 */
@Getter
public class FieldsErrorResponseBody {

  private String apiVersion;
  private List<FieldError> errors;

  public FieldsErrorResponseBody(String apiVersion, List<FieldError> errors){
    this.apiVersion = apiVersion;
    this.errors = errors;
  }
}
