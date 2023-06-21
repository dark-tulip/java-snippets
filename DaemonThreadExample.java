package org.example;

public class DaemonThreadExample {

  public static void main(String[] args) throws InterruptedException {

    Thread daemonThread = new DaemonThread();
    Thread userThread = new UserThread();
    
    // установить поток в качестве демона НУЖНО ДО ЕГО СТАРТА
    daemonThread.setDaemon(true);
    daemonThread.start();

    userThread.setDaemon(false);
    userThread.start();

    userThread.join();
    
    // Если использовать join для демона, он будет ждать работы завершения демона
    // daemonThread.join();

  }
}


class DaemonThread extends Thread {

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()  + " started, is daemon: "
      + Thread.currentThread().isDaemon());
    for (int i = 0; i < 100; i++) {
      try {
        System.out.println(i + "AAAA");
        Thread.sleep(300);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }
}


class UserThread extends Thread {

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()  + " started, is daemon: "
      + Thread.currentThread().isDaemon());

    for (int i = 0; i < 100; i++) {
      System.out.println(i + "BBBB");
    }

  }
}
