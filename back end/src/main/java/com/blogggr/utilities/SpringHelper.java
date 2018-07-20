package com.blogggr.utilities;

import org.springframework.dao.DataAccessException;

/**
 * Created by Daniel Sunnen on 20.07.18.
 */
public class SpringHelper {

  private SpringHelper(){}

  /**
   * For use in service layer
   * @param e
   * @return
   */
  public static RuntimeException convertException(DataAccessException e){
    if (e.getCause() instanceof IllegalArgumentException) {
      //unwrap the original exception Spring wrapped due to @Repository
      return (IllegalArgumentException) e.getCause();
    } else {
      return e;
    }
  }
}
