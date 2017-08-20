package com.blogggr.utilities;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Daniel Sunnen on 07.11.16.
 */
public class TimeUtilities {

  public static Timestamp getCurrentTimestamp() {
    Calendar calendar = Calendar.getInstance();
    java.util.Date now = calendar.getTime();
    return new Timestamp(now.getTime()); //GMT time
  }
}