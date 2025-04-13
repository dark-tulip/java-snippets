package kz.lab.task2;

import kz.lab.annotations.Cached;
import kz.lab.annotations.Setter;

public class Parent implements Base {
  protected String data = "init";

  @Cached
  @Override
  public String method() {
    System.out.println(" == called method from parent class == ");
    return data.toUpperCase();
  }

  @Cached
  @Override
  public String methodCached() {
    System.out.println("Parent compute called");
    return data.toUpperCase();
  }

  @Setter
  @Override
  public void setValue(String data) {
    this.data = data;
  }
}
