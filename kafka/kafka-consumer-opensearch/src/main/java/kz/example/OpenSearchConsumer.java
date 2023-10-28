package kz.example;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * 1) create open search client
 * <p>
 * 2) create kafka client
 * <p>
 * 3) main logic
 * <p>
 * 4) close things
 */
public class OpenSearchConsumer {

  static final Logger logger = LoggerFactory.getLogger(OpenSearchConsumer.class);

  private static KafkaConsumer<String, String> createKafkaConsumer() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "opensearch-consumer-group-1");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new KafkaConsumer<>(properties);
  }

  public static RestHighLevelClient createOpenSearchClient() {
    String connString = "http://localhost:9200";

    // we build a URI from the connection string
    RestHighLevelClient restHighLevelClient;
    URI connUri = URI.create(connString);
    // extract login information if it exists
    String userInfo = connUri.getUserInfo();

    if (userInfo == null) {
      // REST client without security

      var client = RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), "http"));

      return new RestHighLevelClient(client);

    } else {
      // REST client with security
      String[] auth = userInfo.split(":");

      CredentialsProvider cp = new BasicCredentialsProvider();
      cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

      restHighLevelClient = new RestHighLevelClient(
        RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme()))
          .setHttpClientConfigCallback(
            httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(cp)
              .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));
    }

    return restHighLevelClient;
  }

  public static void main(String[] args) throws IOException {
    RestHighLevelClient openSearchClient = createOpenSearchClient();
    CreateIndexRequest createIndexRequest = new CreateIndexRequest("wikimedia");
    KafkaConsumer<String, String> consumer = createKafkaConsumer();

    try (openSearchClient; consumer) {

      boolean isIndexExists = openSearchClient.indices().exists(new GetIndexRequest("wikimedia"), RequestOptions.DEFAULT);

      // проверить наличие индекса в opensearch
      if (!isIndexExists) {
        openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        logger.info("The Wikimedia Index has been created!");
      } else {
        logger.info("Index already exists");
      }

      // подписаться на консюмер
      consumer.subscribe(Collections.singleton("wikimedia.events"));
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1000));

        int count = records.count();

        logger.info("Consumed: " + count + "records");

        for (ConsumerRecord<String, String> record : records) {
          // send record to the opensearch
          try {
            IndexRequest request = new IndexRequest("wikimedia");

            request.source(record.value(), XContentType.JSON);

            IndexResponse response = openSearchClient.index(request, RequestOptions.DEFAULT);

            logger.info("Inserted doc to openSearch: " + response.getId());

          } catch (ElasticsearchStatusException e) {
            // 	Suppressed: org.elasticsearch.client.ResponseException: method [POST], host [http://localhost:9200], URI [/wikimedia/_doc?timeout=1m], status line [HTTP/1.1 400 Bad Request]
            logger.error("record couldn't be send: " + record);
          }
        }
      }
    }
  }

}
