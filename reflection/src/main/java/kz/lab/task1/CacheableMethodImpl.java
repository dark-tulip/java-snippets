package kz.lab.task1;

import kz.lab.annotations.Cached;
import kz.lab.annotations.Setter;

public class CacheableMethodImpl implements CacheableMethod {
  private String value = "Начальное значение";

  @Setter
  public void setValue(String value) {
    this.value = value;
  }

  @Cached
  public String method() {
    System.out.println("Вызов оригинального метода");
    return value;
  }
}
