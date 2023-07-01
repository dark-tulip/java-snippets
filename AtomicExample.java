package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AtomicExample {

    static AtomicInteger sum = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(new IncrementorTask());
        executorService.execute(new IncrementorTask());

        executorService.awaitTermination(1, TimeUnit.SECONDS);

        executorService.shutdown();

//
//        Thread thread1 = new Thread(new IncrementorTask());
//        Thread thread2 = new Thread(new IncrementorTask());
//
//        thread1.start();
//        thread2.start();
//
//        thread1.join();
//        thread2.join();

        System.out.println(sum);
    }
}


class IncrementorTask implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            AtomicExample.sum.incrementAndGet();
        }
    }

}
