package org.example;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.example.processor.BotCountStreamBuilder;
import org.example.processor.EventCountTimeSeriesBuilder;
import org.example.processor.WebsiteCountStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


@Slf4j
public class WikimediaStreamsApp {

  private static final Logger     logger = LoggerFactory.getLogger(WikimediaStreamsApp.class);
  private static final Properties properties;

  private static final String INPUT_TOPIC = "wikimedia.recentchange";

  static {
    properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "wikimedia-stats-application");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
  }

  public static void main(String[] args) {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    KStream<String, String> changeJsonStream = streamsBuilder.stream(INPUT_TOPIC);

    BotCountStreamBuilder botCountStreamBuilder = new BotCountStreamBuilder(changeJsonStream);
    botCountStreamBuilder.setUp();

    WebsiteCountStreamBuilder websiteCountStreamBuilder = new WebsiteCountStreamBuilder(changeJsonStream);
    websiteCountStreamBuilder.setUp();

    EventCountTimeSeriesBuilder eventCountTimeSeriesBuilder = new EventCountTimeSeriesBuilder(changeJsonStream);
    eventCountTimeSeriesBuilder.setUp();

    final Topology appTopology = streamsBuilder.build();
    logger.info("Topology info: {}", appTopology.describe());

    KafkaStreams streams = new KafkaStreams(appTopology, properties);
    streams.start();
  }
}
