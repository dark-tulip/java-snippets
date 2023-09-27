package kz.example;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

// edit configurations -> Modify options -> Allow multiple instances
// поднимите несколько инстанций консюмера и запустите продюсер
// пройдет автоматическая ре-балансировка группы
public class ConsumerDemoWithShutdown {
  static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

  public static void main(String[] args) {

    logger.info("Hello");

    String CONSUMER_GROUP_ID = "group1";
    String TOPIC_NAME = "topic1";

    // create consumer properties
    Properties properties = new Properties();
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
    properties.setProperty("key.deserializer", StringDeserializer.class.getName());
    properties.setProperty("value.deserializer", StringDeserializer.class.getName());
    properties.setProperty("group.id", "none");
    properties.setProperty("auto.offset.reset", "earliest");

    // create consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

    // get the reference to the main thread
    final Thread mainThread = Thread.currentThread();

    // adding the shutdown hook
    // исполняется при нормальном выключении через Ctrl+c
    // или когда закончился последний non-deamon thread завершил работу
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("Shutdown hook executed to close consumers normally");
        // остановить поллинг консюмера
        // вызовет WakeupException в вызываемом потоке
        consumer.wakeup();

        // дождаться завершения работы консюмера с основного потока
        try {
          mainThread.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    // subscribe to a topic
    consumer.subscribe(List.of(TOPIC_NAME));

    System.out.println(consumer.groupMetadata().toString());

    try {
      while (true) {
        ConsumerRecords<String, String> record = consumer.poll(Duration.ofSeconds(1));

        for (var rec : record.records("topic1")) {
          logger.info("Key: " + rec.key() + " Value: " + rec.value()
            + " rec.partition: " + rec.partition() + " offset: " + rec.offset());

        }

      }
    } catch (WakeupException e) {
      logger.info("Consumer waked up from some thread and started to shutdown: " + e.getMessage());
    } catch (Exception e) {
      logger.info(e.getMessage());
    } finally {
      consumer.close();  // commit offsets and close consumer
      logger.info("Consumer gracefully closed");
    }


  }
}
