package org.example.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class WebsiteCountStreamBuilder {
  private final String       WEBSITE_COUNT_STORE = "website-count-store";
  private final String       WEBSITE_COUNT_TOPIC = "wikimedia.stats.website";
  private final ObjectMapper OBJECT_MAPPER       = new ObjectMapper();

  private final KStream<String, String> inputStream;

  public WebsiteCountStreamBuilder(KStream<String, String> kStream) {
    this.inputStream = kStream;
  }

  public void setUp() {
    final TimeWindows timeWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10));

    inputStream
        .selectKey((k, changeJson) -> {
          try {
            final JsonNode jsonNode = OBJECT_MAPPER.readTree(changeJson);
            return jsonNode.get("server_name").asText();
          } catch (IOException e) {
            return "parse_error";
          }
        })
        .groupByKey()
        .windowedBy(timeWindows)
        .count(Materialized.as(WEBSITE_COUNT_STORE))
        .toStream()
        .mapValues((key, value) -> {
          final Map<String, Object> kvMap = Map.of(
              "website", key,
              "count", value
          );
          try {
            return OBJECT_MAPPER.writeValueAsString(kvMap);
          } catch (JsonProcessingException e) {
            return null;
          }
        })
        .to(WEBSITE_COUNT_TOPIC,
            Produced.with(
                WindowedSerdes.timeWindowedSerdeFrom(String.class, timeWindows.size()),
                Serdes.String()
            )
        );
  }
}
