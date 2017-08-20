package com.blogggr.strategies;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface ValidationStrategy {

  boolean inputIsValid(Map<String, String> input, String body);

  String getError();
}
