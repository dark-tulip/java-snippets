package org.example.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class BotCountStreamBuilder {
  private final static String BOT_COUNT_STORE = "bot-count-store";
  private final static String BOT_COUNT_TOPIC = "wikimedia.stats.bots";
  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final KStream<String, String> inputStream;

  public BotCountStreamBuilder(KStream<String, String> inputStream) {
    this.inputStream = inputStream;
  }

  public void setUp() {
    this.inputStream.mapValues(changeJson -> {
      try {
        final JsonNode jsonNode = OBJECT_MAPPER.readTree(changeJson);

        if (jsonNode.get("bot").asBoolean()) {
          return "bot";
        }
        return "non-bot";

      } catch (IOException e) {
        return "parse error";
      }
    })
        .groupBy((key, botOrNot) -> botOrNot)
        .count(Materialized.as(BOT_COUNT_STORE))
        .toStream()
        .mapValues((key, value) -> {
          final Map<String, Long> map = Map.of(String.valueOf(key), value);
          try {
            return OBJECT_MAPPER.writeValueAsString(map);
          } catch (JsonProcessingException e) {
            return null;
          }
        })
        .to(BOT_COUNT_TOPIC);
  }
}
