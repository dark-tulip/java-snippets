package kz.lab.task2;

import kz.lab.annotations.Cached;
import kz.lab.annotations.Setter;

public class Child extends Parent {

  /**
   * Если переопределяется метод и на него
   * не поставлена аннотация @Cache — считается, что кэширование не нужно.
   */
  @Override
  public String method() {
    System.out.println(" == called method from Child class == ");
    return "[" + data.toLowerCase() + "]";
  }


  /**
   * Будем брать из кэша если там есть сохраненное значение.
   */
  @Cached
  @Override
  public String methodCached() {
    System.out.println(" == called: methodV2Cached");
    return "[" + data + "]";
  }

  /**
   * Не помечен @Setter — кэш не сбрасывается
   */
  @Override
  public void setValue(String data) {
    this.data = data;
  }

  /**
   * помечен @Setter — кэш сбрасывается
   */
  @Setter
  public void setValueWithCacheEvict(String data) {
    this.data = data;
  }
}
