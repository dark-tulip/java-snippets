- Планировщик в операционной системе определяет какие потоки будут исполняться на данный момент времени
- У любого объекта можно вызвать wait и у любого объекта можно вызвать notifyall
- Wait() заставляет поток ждать, пока кто то не вызовет его с помощью notify()
- Javadoc: если вы хотите усыпить поток для его ожидания, заблокируйте монитор его объекта
- В Java в каждом объекте есть заголовок, это метаинформация которая нужна в jvm. Там же хранится информация о ГС и информация, нужная для блокировок, в каком сотоянии находится поток, thin, biased, inflated

## Concurrency and parallelizm
- конкурентность выполнение нескольких задач. В разных процессорах достигается разными способами
- параллелизм выполнение нескольких задач одновременно

## Sync and async
- за счет асинхронного программирования достигается concurrency
- синхронное программирование это строгая последовательность в выполнении друг за другом

## Thread pool and executor service
- метод `execute()` передает задачу в `thread pool` где оно исполняется одним из потоков
- после вызова shutdown executor service перестает принимать новые задачи, и завершает текущие
- `awaitTermination` - либо `ExecutorService` прекращает работу либо истекает таймаут


## Runnable and Callable
- Runnable эта передача некоторых инструкций отдельному потоку
- Любая блокировка идет на объекте а не на классе 
- `Callable vs Runnable` - runnable returns Void, callable returns Feature
- `Runnable` это спопсоб передать в аргумент void-овский метод
- метод `.join()` заставляет вызвавший родительский поток (MainThread) дождаться завершения дочернего (ChildThread)
```java
Main thread started...
JThread  started... 
JThread  finished... 
Main thread finished...
```
- если необходимо завершить Main после всех дочерних, для каждого дочернего нужно вызвать `.join()` метод

- synchronized гарантирует освобождение монитора доступа к объекту ТОЛЬКО  после завершения synchronized метода 
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
```

- реализация класса обертки возьмет вверх над объектом при переопределении метода
```Java
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
