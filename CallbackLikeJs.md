```java
package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
class MyUser {
  private String  name;
  private Integer age;
}

public class CallbackLikeJs {

  public static void main(String[] args) {

    hello(new MyUser("testName", 11), myUser -> {
      System.out.println("Something wrong with user: " + myUser);
    });

    hello(new MyUser(null, 11), myUser -> {
      System.out.println("Something wrong with user: " + myUser);
    });

    hello2(new MyUser(null, 11), () -> {
      System.out.println("Provided name or age is null");
    });

  }

  public static void hello(MyUser user, Consumer<MyUser> callback) {
    if (user.getName() == null || user.getAge() == null) {
      callback.accept(user);
    } else {
      System.out.println("OK, user: " + user);
    }
  }
  
  public static void hello2(MyUser user, Runnable callback) {
    if (user.getName() == null || user.getAge() == null) {
      callback.run();
    } else {
      System.out.println("OK, user: " + user);
    }
  }

}
```
