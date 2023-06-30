package org.example;

import java.util.concurrent.*;

public class SumNumbers {

  public static long variable = 100_000_000;
  public static long sum = 0;

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    ExecutorService executors = Executors.newFixedThreadPool(10);

    for (int i = 0; i < 10; i++) {
      // расспараллелить частичное нахождение суммы чисел
      Future<Long> result = executors.submit(new PartialSum(variable * i, variable * (i + 1)));
      sum += result.get();
    }

    executors.shutdown();
    System.out.println(sum);
    assert sum == 5000000050000000L;
  }

}

class PartialSum implements Callable<Long> {
  long from;
  long to;

  PartialSum(long from, long to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public Long call() {
    long partSum = 0;
    for (long i = from; i <= to; i++) {
      partSum += i;
    }
    return partSum;
  }
}
