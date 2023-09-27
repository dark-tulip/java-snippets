package kz.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.ConsumerCoordinator;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerDemo {
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

    // none / earliest / latest
    properties.setProperty("group.id", "none");
    properties.setProperty("auto.offset.reset", "earliest");

    // create consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

    // subscribe to a topic
    consumer.subscribe(List.of(TOPIC_NAME));

    System.out.println(consumer.groupMetadata().toString());

    while (true) {
      ConsumerRecords<String, String> record = consumer.poll(Duration.ofSeconds(1));

      for (var rec : record.records("topic1")) {
        logger.info("Key: " + rec.key() + " Value: " + rec.value()
          + " rec.partition: " + rec.partition() + " offset: " +rec.offset());

      }

    }

  }
}
