package org.example;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Потокобезопасная очередь с ограниченным размером (capacity restricted)
 *
 *
 * Producer inserted elem: 1, now array: [1]
 * Producer inserted elem: 2, now array: [1, 2]
 * Producer inserted elem: 3, now array: [1, 2, 3]
 * Consumer2 took element: 2
 * Consumer took element: 1
 * Producer inserted elem: 4, now array: [3, 4]
 * Producer inserted elem: 5, now array: [3, 4, 5]
 * Consumer2 took element: 3
 * Consumer took element: 4
 * Producer inserted elem: 6, now array: [5, 6]
 * Producer inserted elem: 7, now array: [5, 6, 7]
 * Producer inserted elem: 8, now array: [5, 6, 7]
 */
public class BlockingArrayQueueEx {

  public static void main(String[] args) {

    ArrayBlockingQueue<String> arr = new ArrayBlockingQueue<>(3);

    Thread producer = new Thread(() -> {
      int i = 0;
      while (true) {
        try {
          String offeredElem = "" + ++i;
          // offer добавляет элемент в конец очереди, дожидаясь освобождения места в ограниченном массиве
          // в отличие от add() не кидает IllegalStateException если очередь полна, а просто дожидается ее
          // peek() - берет головной элемент из очереди, НО НЕ удаляет его, возвращает null при его отсутствии
          // poll() - берет головной элемент из очереди, И удаляет его, возвращает null при его отсутствии
          // put() ждет бесконечно долго, пока не освободится место
          arr.offer(offeredElem);
           System.out.println("Producer inserted elem: " + offeredElem + ", now array: " + arr);
          Thread.sleep(400);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    producer.start();


    Thread consumer = new Thread(() -> {
      while(true) {
        try {
          // take забирает элементы из начала очереди
          Thread.sleep(1000);
          String el = arr.take();
          System.out.println("Consumer took element: " + el );
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    consumer.start();

    Thread consumer2 = new Thread(() -> {
      while(true) {
        try {
          Thread.sleep(1000);
          String el = arr.take();
          System.out.println("Consumer2 took element: " + el);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    consumer2.start();

  }

}
