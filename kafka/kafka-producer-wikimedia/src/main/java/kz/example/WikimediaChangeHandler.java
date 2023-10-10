package kz.example;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaChangeHandler implements BackgroundEventHandler {

  KafkaProducer<String, String> producer;

  String topicName;

  public WikimediaChangeHandler(KafkaProducer<String, String> producer, String topicName) {
    this.topicName = topicName;
    this.producer = producer;
  }

  private static final Logger logger = LoggerFactory.getLogger(WikimediaChangeHandler.class);

  @Override
  public void onOpen() throws Exception {
    logger.info("MZVANUO2 :: WikimediaChangeHandler opened");
  }

  @Override
  public void onClosed() throws Exception {
    producer.close();
  }

  @Override
  public void onMessage(String event, MessageEvent messageEvent) {
    System.out.println(event);
    System.out.println(messageEvent + "\n\n");

    // works async
    producer.send(new ProducerRecord<>(topicName, messageEvent.getData()));
  }

  @Override
  public void onComment(String comment) {

  }

  @Override
  public void onError(Throwable t) {
    logger.error("B9OB75DT :: Error in stream reading: " + t);
  }
}
