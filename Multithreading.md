```Java
/**
 * Thread-0 x=2 y=2
 * Thread-0 x=3 y=3
 * Thread-0 x=4 y=4
 * Thread-0 x=5 y=5
 * Thread-0 x=6 y=6
 * Thread-1 x=7 y=7
 * Thread-1 x=8 y=8
 * Thread-1 x=9 y=9
 * Thread-1 x=10 y=10
 * Thread-1 x=11 y=11
 */
public class Main implements Runnable {
  private int x = 1;
  private int y = 1;

  public static void main(String[] args) {
    Main main = new Main();

    // synchronized гарантирует освобождение монитора доступа к объекту
    // после завершения start()
    new Thread(main).start();
    new Thread(main).start();
  }
  @Override
  public synchronized void run() {
    for (int i = 0; i < 5; i++) {
      x++;
      y++;
      System.out.println(Thread.currentThread().getName() +  " x=" + x + " y=" + y);
    }
  }
}


class Test extends Thread {
  public Test() {}
  public Test(Runnable r) {
    super(r);
  }

  public void run() {
    System.out.println("Inside Test");
  }

}


/**
 *  Inside Test
 *  Inside Test
 */
class CallWrappedClass {
  public static void main(String[] args) {
    // реализация класса обертки возьмет вверх над объектом
    new Test().start();
    new Test(new RunnableImpl()).start();
  }
}
class RunnableImpl implements Runnable {
  public void run() {
    System.out.println("Inside runnable");
  }
}
```
