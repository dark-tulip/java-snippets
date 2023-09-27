package kz.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class ProducerDemoKeys {
  static final Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);

  public static void main(String[] args) {
    // create producer properties
    Properties properties = new Properties();

    // connect to local server property (unsecure)
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

    // add properties to serialize values and keys
    properties.setProperty("key.serializer", StringSerializer.class.getName());
    properties.setProperty("value.serializer", StringSerializer.class.getName());
    properties.setProperty("batch.size", "128");

    // create thr Producer <Key, Value> of message record
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    for (int j = 0; j < 10; j++) {
      for (int i = 0; i < 10; i++) {
//      ProducerRecord<String, String> record3 = new ProducerRecord<>("topic1", "key: " + i, "value: " + i);
        ProducerRecord<String, String> record3 = new ProducerRecord<>("topic1", "value: " + i);

        producer.send(record3, (metadata, exception) -> {
          // выполняется каждый раз как отправляется сообщение или вызвано исключение при отправке
          if (exception == null) {
            logger.info("Topic: " + metadata.topic()
              + "\npartition: " + metadata.partition()
              + "\noffset: " + metadata.offset()
              + "\ntimestamp: " + metadata.timestamp()
            );
          } else {
            logger.error("Error while send", exception);
            exception.printStackTrace();
          }

        });

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // все что есть в буфере принудительно отправит и будет дожидаться while response will complete (до этого времени заблокирует поток)
    producer.flush();

    // закроем продюсер (будет ждать завершения текущих запросов,
    // для callback исполнится моментально - потому что коллбэк будет ждать завершения своей нити вечно
    producer.close();
  }
}
