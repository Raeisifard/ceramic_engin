package com.vx6.utils;

import io.vertx.core.json.JsonObject;

public class Cast {
  public static boolean Boolean(JsonObject jo, String n) {
    boolean b = false;
    try {
      b = jo.getBoolean(n);
    } catch (Exception e) {
      try {
        b = jo.getInteger(n) == 1;
      } catch (Exception ee) {
        try {
          b = Boolean.parseBoolean(jo.getString(n));
        } catch (Exception eee) {
          e.printStackTrace();
        }
      }
    }
    return b;
  }

  public static String String(JsonObject jo, String n) {
    String b;
    try {
      b = jo.getInteger(n) + "";
      return b;
    } catch (Exception e) {
      try {
        b = jo.getString(n);
        return b;
      } catch (Exception f) {
        b = jo.getBoolean(n).toString();
        return b;
      }
    }
  }

  public static JsonObject JsonObject(String str) {
    try {
      return new JsonObject(str);
    } catch (Exception e) {
      return null;
    }
  }
}
