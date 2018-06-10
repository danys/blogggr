package com.blogggr.validators;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class GenericValidator {

  protected String errorMessage;
  private final Log logger = LogFactory.getLog(this.getClass());

  public boolean stringIsNumber(String numStr) {
    try {
      Long.parseLong(numStr);
    } catch (NumberFormatException e) {
      //Not a valid number
      return false;
    }
    return true;
  }

  public boolean inputIsValid(Map<String, String> input, String body) {
    try {
      return validate(input, body);
    } catch (JsonParseException e) {
      errorMessage = "JSON parse exception";
      return false;
    } catch (JsonProcessingException e) {
      errorMessage = "JSON processing exception";
      return false;
    } catch (IOException e) {
      errorMessage = "JSON input exception";
      return false;
    }
  }

  protected boolean validate(Map<String, String> input, String body)
      throws JsonParseException, JsonProcessingException, IOException{return true;}


  public final String getError() {
    return errorMessage;
  }
}
