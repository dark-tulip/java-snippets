package org.example;


import java.util.concurrent.CountDownLatch;

import static org.example.MyJob.*;

// блочить исполнение пока не выполнится заданное количество методов
public class CountDownLatchExample {

    static CountDownLatch countDownLatch = new CountDownLatch(3);

    public static void main(String[] args) throws InterruptedException {
        // треды не запустятся, пока не исполнятся три нижестоящие подготовительные задачи
        new MyJob("jjob11", countDownLatch);
        new MyJob("ewfwef", countDownLatch);
        new MyJob("3333232", countDownLatch);
        new MyJob("55555555555", countDownLatch);

        firstTask();
        secondTask();
        thirdTask();
    }

    static void firstTask() throws InterruptedException {
        sleep(2000);
        System.out.println(" :: firstTask  countDownLatch.getCount(): " + countDownLatch.getCount());
        countDownLatch.countDown();
    }

    static void secondTask() throws InterruptedException {
        sleep(2000);
        System.out.println(" :: secondTask  countDownLatch.getCount(): " + countDownLatch.getCount());
        countDownLatch.countDown();

    }

    static void thirdTask() throws InterruptedException {
        sleep(2000);
        System.out.println(" :: thirdTask  countDownLatch.getCount(): " + countDownLatch.getCount());
        countDownLatch.countDown();

    }

}

class MyJob extends Thread {

    String name;

    private CountDownLatch countDownLatch;

    public MyJob(String name, CountDownLatch countDownLatch) {
        this.name = name;
        this.countDownLatch = countDownLatch;
        this.start();
    }

    public void run() {
        System.out.println("Job started " + getName());
        try {
            countDownLatch.await();
            System.out.println(" :: everything ready for job");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Job End " + getState());
    }

}
