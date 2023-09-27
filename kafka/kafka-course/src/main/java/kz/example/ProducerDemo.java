package kz.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
  static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

  public static void main(String[] args) {

    logger.info("Hello");

    // create producer properties
    Properties properties = new Properties();

    // connect to local server property (unsecure)
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

    // add properties to serialize values and keys
    properties.setProperty("key.serializer", StringSerializer.class.getName());
    properties.setProperty("value.serializer", StringSerializer.class.getName());


    properties.setProperty("linger.ms", "10");
    properties.setProperty("batch.size", "1");

    // create thr Producer <Key, Value> of message record
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


    ProducerRecord<String, String> record = new ProducerRecord<>("topic1", "hello");
    ProducerRecord<String, String> record2 = new ProducerRecord<>("topic1", "helloKey", "helloValue");
    ProducerRecord<String, String> record3 = new ProducerRecord<>("topic1", 0, "helloKey1", "helloValue1");

    // асинхронно отправляет значение в топик
    producer.send(record);
    producer.send(record2);
    producer.send(record3);

    // все что есть в буфере принудительно отправит и будет дожидаться while response will complete (до этого времени заблокирует поток)
    producer.flush();

    // закроем продюсер (будет ждать завершения текущих запросов,
    // для callback исполнится моментально - потому что коллбэк будет ждать завершения своей нити вечно
    producer.close();
  }
}
