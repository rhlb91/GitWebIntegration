package com.teammerge.utils;

import java.util.concurrent.TimeUnit;

public class LoggerUtils {

  public static String getTimeInMilliSecs(long start, long end) {
    return TimeUnit.MILLISECONDS.toMillis(end - start) + " msecs";
  }

  public static String getTimeInSecs(long start, long end) {
    return TimeUnit.MILLISECONDS.toSeconds(end - start) + " secs";
  }

  public static String getTimeInMins(long start, long end) {
    return TimeUnit.MILLISECONDS.toMinutes(end - start) + " mins";
  }


}
