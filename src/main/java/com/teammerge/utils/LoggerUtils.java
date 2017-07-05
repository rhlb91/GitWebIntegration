package com.teammerge.utils;

public class LoggerUtils {

  public static String getTimeInSecs(long start, long end) {
    return (end - start) / (1000.0 * 60) + " secs";
  }

  public static String getTimeInMins(long start, long end) {
    return (end - start) / (1000.0 * 60 * 60) + " mins";
  }

}
