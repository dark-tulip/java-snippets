package kz.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class ProducerDemoWithCallback {
  static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

  public static void main(String[] args) {
    // create producer properties
    Properties properties = new Properties();

    // connect to local server property (unsecure)
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

    // add properties to serialize values and keys
    properties.setProperty("key.serializer", StringSerializer.class.getName());
    properties.setProperty("value.serializer", StringSerializer.class.getName());

    // свойство
    properties.setProperty("partitioner.class", RoundRobinPartitioner.class.getName());
    properties.setProperty("partitioner.class", UniformStickyPartitioner.class.getName());  // устарел, чтобы его использовать
    properties.setProperty("linger.ms", "0");
    properties.setProperty("batch.size", "256");
    properties.setProperty("retries", "2");
    properties.setProperty("request.timeout.ms", "1000");
    properties.setProperty("delivery.timeout.ms", "3000");
    properties.setProperty("transaction.timeout.ms", "3000");
    properties.setProperty("max.block.ms", "4000");

    // create thr Producer <Key, Value> of message record
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    ProducerRecord<String, String> record = new ProducerRecord<>("topic1", "111");
    ProducerRecord<String, String> record2 = new ProducerRecord<>("topic1", "key222", "222");

    // асинхронно отправляет значение в топик
    producer.send(record, (metadata, exception) -> System.out.println("Send: "+ metadata.topic()));
    producer.send(record2, (metadata, exception) -> {
      if (exception != null) {
        exception.printStackTrace();
      } else {
        System.out.println("Send: " + metadata.topic());
      }
    });

    for (int i = 0; i < 10; i++) {
      ProducerRecord<String, String> record3 = new ProducerRecord<>("topic1", "key" + i, "value: " + i);

      producer.send(record3, new Callback() {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
          // выполняется каждый раз как отправляется сообщение или вызвано исключение при отправке
          if (exception == null) {
            logger.info("Record was successfully sent\nTopic: " + metadata.topic()
              + "\npartition: " + metadata.partition()
              + "\noffset: " + metadata.offset()
              + "\ntimestamp: " + metadata.timestamp()
              + "\ndate: " + new Date(metadata.timestamp())
            );
          } else {
            // Такого номера партиции в топике нет, но пишет что Topic topic1 not present (сработало время - "max.block.ms": "4000")
            // [main] ERROR kz.example.ProducerDemoWithCallback - Error while send
            // org.apache.kafka.common.errors.TimeoutException: Topic topic1 not present in metadata after 4000 ms.
            logger.error("Error while send", exception);
            exception.printStackTrace();
          }

        }
      });

      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    // все что есть в буфере принудительно отправит и будет дожидаться while response will complete (до этого времени заблокирует поток)
    producer.flush();

    // закроем продюсер (будет ждать завершения текущих запросов,
    // для callback исполнится моментально - потому что коллбэк будет ждать завершения своей нити вечно
    producer.close();
  }
}
