package kz.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerDemoCooperativeStickyAssignor {
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
    properties.setProperty("group.id", CONSUMER_GROUP_ID);
    properties.setProperty("auto.offset.reset", "earliest");

    // preferred to use
    properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

    // задав эту настройку мы узнаем об упавшем консюмере только через 45 сек
    properties.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "444");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

    final Thread mainThread = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("Shutdown hook executed to close consumers normally");
        consumer.wakeup();
        try {
          mainThread.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    consumer.subscribe(List.of(TOPIC_NAME));

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
      consumer.close();
      logger.info("Consumer gracefully closed");
    }
  }
}
