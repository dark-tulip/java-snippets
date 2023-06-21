package org.example;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorService {

  public static void main(String[] args) throws InterruptedException {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    // период, после истечения времени, неважно завершив задачу, или нет
    executorService.scheduleAtFixedRate(new RunnableTask1(), 1, 200, TimeUnit.MILLISECONDS);

    // период, после завершения потоком текущей задачи
    executorService.scheduleWithFixedDelay(new RunnableTask2(), 1, 1, TimeUnit.SECONDS);

    Thread.sleep(1000);
    executorService.shutdown();
  }

}


class RunnableTask1 implements Runnable {
  @Override
  public void run() {
    System.out.println("L6RRKXSU :: I am started " + Thread.currentThread().getName());
  }
}


class RunnableTask2 implements Runnable {
  @Override
  public void run() {
    System.out.println("0WWEOLB0 :: I am started " + Thread.currentThread().getName());
  }
}
