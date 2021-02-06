package com.vx6.utils;

public class Convert {
  public static boolean toBoolean(String value) {
    boolean returnValue = false;
    if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) ||
      "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
      returnValue = true;
    return returnValue;
  }
  public static boolean toBoolean(Integer value) {
    return value > 0;
  }
}
