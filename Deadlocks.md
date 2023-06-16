### Пример работы deadlocks in Java

``` Java
/**
Output:
Thread1 start 
Thread2 start 
...
*/
public class Deadlock {

  public static void main(String[] args) {
    Object lock1 = new Object();
    Object lock2 = new Object();

    Thread thread1 = new Thread(() -> {
      System.out.println(Thread.currentThread().getName() + " start ");
      synchronized (lock1) {
        // Один поток должен дождаться пока второй освободит свой монитор
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        synchronized (lock2) { }
      }
      System.out.println(Thread.currentThread().getName() + " end ");
    }, "Thread1");

    Thread thread2 = new Thread(() -> {
      System.out.println(Thread.currentThread().getName() + " start ");
      synchronized (lock2) {
        synchronized (lock1) { }
      }
      System.out.println(Thread.currentThread().getName() + " end ");

    }, "Thread2");

    thread1.start();
    thread2.start();
  }
}
```

``` Java
/**
Output:
pool-1-thread-2 start
pool-1-thread-1 start
pool-1-thread-2 end
pool-1-thread-1 end
*/
public class DeadlockComplex {

  public static void main(String[] args) {
    ArrayList<Resource> resourceArraysList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      resourceArraysList.add(new Resource(i));
    }

    // Использование компоратора позволяет упорядочить ресурсы, тем самым упорядочивая порядок захвата мониторов
    Comparator<Resource> comparator = Comparator.comparingInt(resourceArraysList::indexOf);

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.execute(() -> handle(resourceArraysList.get(0), resourceArraysList.get(1), comparator));
    executorService.execute(() -> handle(resourceArraysList.get(1), resourceArraysList.get(0), comparator));
  }

  public static void handle(Resource res1, Resource res2, Comparator comparator) {
    Object lock1 = res1;
    Object lock2 = res2;
    if (comparator.compare(res1, res2) < 0) {
      lock2 = res1;
      lock1 = res2;
    }
    System.out.println(Thread.currentThread().getName() + " start");

    synchronized (lock1) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      synchronized (lock2) {
        res1.setValue(res2.getValue());
      }
    }
    System.out.println(Thread.currentThread().getName() + " end");
  }

  static class Resource {
    int value;

    public Resource(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }
}
```
