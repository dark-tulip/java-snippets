package kz.example;


import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaChangesProducer {

  private static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";

  private static final URI uri = URI.create("https://stream.wikimedia.org/v2/stream/recentchange");

  // топик для храненя event-ов
  private static final String topic = "wikimedia.events";

  public static void main(String[] args) throws InterruptedException {

    // declare properties
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // create the producer
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    // логика обработки каждого события
    BackgroundEventHandler eventHandler = new WikimediaChangeHandler(producer, topic);  // обработчик событий

    // потоковый источник событий
    EventSource.Builder eventSourceBuilder = new EventSource.Builder(uri);

    // обертка для обработки каждого события
    BackgroundEventSource.Builder builder = new BackgroundEventSource.Builder(eventHandler, eventSourceBuilder);

    // open connection (объявление SSE клинета - единонаправленное соед к клинету)
    try (BackgroundEventSource eventSource = builder.build()) {
      // запустить worker thread
      eventSource.start();

      TimeUnit.MINUTES.sleep(1);
    }

  }
}
