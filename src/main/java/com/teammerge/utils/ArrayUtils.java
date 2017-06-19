package com.teammerge.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for arrays and collections.
 *
 * 
 *
 */
public class ArrayUtils {

  public static boolean isEmpty(byte[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isEmpty(char[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static String toString(Collection<?> collection) {
    if (isEmpty(collection)) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (Object o : collection) {
      sb.append(o.toString()).append(", ");
    }
    // trim trailing comma-space
    sb.setLength(sb.length() - 2);
    return sb.toString();
  }

  public static Collection<String> fromString(String value) {
    if (StringUtils.isEmpty(value)) {
      value = "";
    }
    List<String> list = new ArrayList<String>();
    String[] values = value.split(",|;");
    for (String v : values) {
      String string = v.trim();
      if (!StringUtils.isEmpty(string)) {
        list.add(string);
      }
    }
    return list;
  }

  public static <X> List<X> join(List<X>... elements) {
    List<X> list = new ArrayList<X>();
    for (List<X> element : elements) {
      list.addAll(element);
    }
    return list;
  }

  public static <X> List<X> join(X[]... elements) {
    List<X> list = new ArrayList<X>();
    for (X[] element : elements) {
      list.addAll(Arrays.asList(element));
    }
    return list;
  }
}
