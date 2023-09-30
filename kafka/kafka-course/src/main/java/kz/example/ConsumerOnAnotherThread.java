package kz.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Способ запуска консюмера в отдельном потоке.
 * Сначала вызывается wakeup из метода shutdown,
 * затем закрывается ресурс и выполняется плавное завершение программы
 */
public class ConsumerOnAnotherThread {

  public static void main(String[] args) {

    ConsumerWorker consumerWorker = new ConsumerWorker();

    new Thread(consumerWorker).start();

    System.out.println("ZSGSYNH1 :: " + Thread.currentThread().getName());

    Runtime.getRuntime().addShutdownHook(new Thread(new ConsumerCloser(consumerWorker)));
  }

  private static class ConsumerCloser implements Runnable {
    private final ConsumerWorker consumerWorker;

    ConsumerCloser(final ConsumerWorker consumerWorker) {
      this.consumerWorker = consumerWorker;
    }

    @Override
    public void run() {

      System.out.println("ZSGSYNH3 :: " + Thread.currentThread().getName());

      try {
        consumerWorker.shutdownConsumer();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class ConsumerWorker implements Runnable {
    private Consumer<String, String> consumer;
    private CountDownLatch countDownLatch;

    private final Logger log = LoggerFactory.getLogger(ConsumerWorker.class);

    @Override
    public void run() {
      System.out.println("ZSGSYNH2 :: " + Thread.currentThread().getName());

      countDownLatch = new CountDownLatch(1);

      Properties properties = new Properties();
      properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://127.0.0.1:9092");
      properties.setProperty("key.deserializer", StringDeserializer.class.getName());
      properties.setProperty("value.deserializer", StringDeserializer.class.getName());
      properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-thread");

      consumer = new KafkaConsumer<>(properties);
      consumer.subscribe(List.of("topic1"));

      try {
        while (true) {
          ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
          for (var record : records) {
            System.out.println("key: " + record.key() + " value: " + record.value());
          }

        }
      } catch (WakeupException e) {
        log.info("Consumer was waked up (stopped polling)");
      } catch (Exception e) {
        log.error(e.getMessage());
      } finally {

        // закрыть ресурсы кафка консюмер стрима
        consumer.close();

        // снизить счетчик ожидания
        countDownLatch.countDown();
      }
    }

    void shutdownConsumer() throws InterruptedException {

      // прекратить поллинг -> закрыть ресурс -> дождаться countDown()
      consumer.wakeup();

      // ожидать пока ресурс кафки консюмера закроется
      countDownLatch.await();
      log.info("Q1Y35EN8 :: shutdownConsumer");
    }
  }
}


