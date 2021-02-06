package com.vx6.tools;

public class VueComponent {
  private String name;
  private String as;

  public VueComponent() {

  }

  public VueComponent(String name, String as) {
    this.name = name;
    this.as = as;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAs() {
    return as;
  }

  public void setAs(String as) {
    this.as = as;
  }
}
