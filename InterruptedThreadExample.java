package org.example;

/**
 Output:
 false
 thread started
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: false
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 ? is called interruption: true
 Thread end with sum: 7000.357133746821
 */

public class InterruptedThreadExample {
  public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(() -> {

      System.out.println("thread started");

      double accumulator = 0;

      for (int i = 0; i < 100; i++) {
        accumulator += Math.hypot(i, i);
        System.out.println("? is called interruption: " + Thread.currentThread().isInterrupted());

      }

      System.out.println("Thread end with sum: " + accumulator);

    });

    thread.start();
    System.out.println(thread.isInterrupted());

    Thread.sleep(3);
    // убивает поток неккорректно завершив его работу
//    thread.stop();

    // убивает поток, корректно завершив его работу.
    // Кидает исключение если использованы методы сна (поток хотят прервать когда он спит)
    // - нужна обработка вручную и проверка состояния внутри метода run()
    thread.interrupt();
    
    System.out.println(thread.isInterrupted());

  }
}
