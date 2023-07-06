package org.example;

import java.util.HashMap;
import java.util.List;

public class CopyOnWriteArrayList extends HashMap {

  // Example1 периодично будет возникать concurrentModificationException
  public static void main(String[] args) {

    // при замене с arrayList на CopyOnWriteArrayList все операции будет исполняться своим порядком
    List<String> lst = new java.util.concurrent.CopyOnWriteArrayList<>();

    lst.add("user1");
    lst.add("user2");
    lst.add("user3");
    lst.add("user4");

    Thread reader = new Thread(() -> {
      // cоздаем итератор
      var now = lst.iterator();

      while (now.hasNext()) {
        try {
          lst.remove("user1");
          lst.add(0, "NEW user2");
          System.out.println(now.next());  // дернуть следующий и напечатать

          Thread.sleep(500);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    Thread writer = new Thread(() -> {
      try {
        Thread.sleep(0);
        lst.remove("user1");
        lst.add(0, "NEW user1");
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    reader.start();
    writer.start();

    try {
      reader.join();
      writer.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    System.out.println(lst);
  }
}
