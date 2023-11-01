package kz.example;

import com.google.gson.JsonParser;
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
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 1) create open search client
 * <p>
 * 2) create kafka client
 * <p>
 * 3) main logic
 * <p>
 * 4) close things
 */
public class OpenSearchConsumerBulkRequest {

  static final Logger logger = LoggerFactory.getLogger(OpenSearchConsumerBulkRequest.class);

  private static KafkaConsumer<String, String> createKafkaConsumer() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "opensearch-consumer-group-1");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

    return new KafkaConsumer<>(properties);
  }

  public static RestHighLevelClient createOpenSearchClient() {
    String connString = "http://localhost:9200";

    // we build a URI from the connection string
    RestHighLevelClient restHighLevelClient;
    URI                 connUri = URI.create(connString);
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

      restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme())).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(cp).setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));
    }

    return restHighLevelClient;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    RestHighLevelClient           openSearchClient   = createOpenSearchClient();
    CreateIndexRequest            createIndexRequest = new CreateIndexRequest("wikimedia");
    KafkaConsumer<String, String> consumer           = createKafkaConsumer();

    try (openSearchClient; consumer) {

      boolean isIndexExists = openSearchClient.indices()
                                              .exists(new GetIndexRequest("wikimedia"), RequestOptions.DEFAULT);

      // проверить наличие индекса в opensearch
      if (!isIndexExists) {
        openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        logger.info("The Wikimedia Index has been created!");
      } else {
        logger.info("Index already exists");
      }

      // подписаться на консюмер
      consumer.subscribe(Collections.singleton("wikimedia.events"));

      long now = System.currentTimeMillis();

      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1000));

        int count = records.count();

        logger.info("Consumed: " + count + " records");

        BulkRequest bulkRequest = new BulkRequest();

        for (ConsumerRecord<String, String> record : records) {

          // send record to the opensearch
          String msgId = parseRecordId(record.value());
          try {
            IndexRequest request = new IndexRequest("wikimedia");

            request.source(record.value(), XContentType.JSON)
                   .id(msgId);

            bulkRequest.add(request);

          } catch (ElasticsearchStatusException e) {
            // 	Suppressed: org.elasticsearch.client.ResponseException: method [POST], host [http://localhost:9200], URI [/wikimedia/_doc?timeout=1m], status line [HTTP/1.1 400 Bad Request]
            logger.error("record couldn't be send: " + record);
          }
        }

        if (bulkRequest.numberOfActions() > 0) {
          BulkResponse response = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);

          logger.info("Inserted: " + response.getItems().length + " document(s)");

          if (response.hasFailures()) {
            logger.info("Failed: " + response.buildFailureMessage());
            // [399]: index [wikimedia], type [_doc], id [39d9796d-3e59-4a15-8a06-1287e789238f],
            // message [ElasticsearchException[Elasticsearch exception [type=mapper_parsing_exception, reason=object mapping for
            // [log_params] tried to parse field [null] as object, but found a concrete value]]]

          }

          /*
           * [main] INFO kz.example.OpenSearchConsumerBulkRequest - bulkRequest.numberOfActions(): 500
           * [main] INFO kz.example.OpenSearchConsumerBulkRequest - bulkRequest.getRefreshPolicy(): NONE
           * [main] INFO kz.example.OpenSearchConsumerBulkRequest - bulkRequest.getIndices(): [wikimedia]
           */
          logger.info("bulkRequest.numberOfActions(): " + bulkRequest.numberOfActions());
          logger.info("bulkRequest.getRefreshPolicy(): " + bulkRequest.getRefreshPolicy());
          logger.info("bulkRequest.getIndices(): " + bulkRequest.getIndices());

          /*
           * response.getItems(): [org.elasticsearch.action.bulk.BulkItemResponse@859ea42, org.elasticsearch.action.bulk.BulkItemResponse@28737371, org.elasticsearch.action.bulk.BulkItemResponse@2af46afd, org.elasticsearch.action.bulk.BulkItemResponse@760245e1, org.elasticsearch.action.bulk.BulkItemResponse@31ceba99, org.elasticsearch.action.bulk.BulkItemResponse@295bf2a, org.elasticsearch.action.bulk.BulkItemResponse@bbf9e07, org.elasticsearch.action.bulk.BulkItemResponse@6ff6efdc, org.elasticsearch.action.bulk.BulkItemResponse@2b289ac9, org.elasticsearch.action.bulk.BulkItemResponse@4eb1c69, org.elasticsearch.action.bulk.BulkItemResponse@73d3e555, org.elasticsearch.action.bulk.BulkItemResponse@2b037cfc, org.elasticsearch.action.bulk.BulkItemResponse@44924587, org.elasticsearch.action.bulk.BulkItemResponse@18460128, org.elasticsearch.action.bulk.BulkItemResponse@74d3b638, org.elasticsearch.action.bulk.BulkItemResponse@4487c0c2, org.elasticsearch.action.bulk.BulkItemResponse@126f1ba8, org.elasticsearch.action.bulk.BulkItemResponse@3a08078c, org.elasticsearch.action.bulk.BulkItemResponse@53830483, org.elasticsearch.action.bulk.BulkItemResponse@29f85fe1, org.elasticsearch.action.bulk.BulkItemResponse@43a0a32d, org.elasticsearch.action.bulk.BulkItemResponse@396ef8b2, org.elasticsearch.action.bulk.BulkItemResponse@72825400, org.elasticsearch.action.bulk.BulkItemResponse@19ee1ae6, org.elasticsearch.action.bulk.BulkItemResponse@5f117b3d, org.elasticsearch.action.bulk.BulkItemResponse@1174a305, org.elasticsearch.action.bulk.BulkItemResponse@71b6d77f, org.elasticsearch.action.bulk.BulkItemResponse@1866da85, org.elasticsearch.action.bulk.BulkItemResponse@4cfa8227, org.elasticsearch.action.bulk.BulkItemResponse@78226c36, org.elasticsearch.action.bulk.BulkItemResponse@3f685162, org.elasticsearch.action.bulk.BulkItemResponse@11f406f8, org.elasticsearch.action.bulk.BulkItemResponse@987455b, org.elasticsearch.action.bulk.BulkItemResponse@622fdb81, org.elasticsearch.action.bulk.BulkItemResponse@1f3165e7, org.elasticsearch.action.bulk.BulkItemResponse@2ec3633f, org.elasticsearch.action.bulk.BulkItemResponse@1d5d5621, org.elasticsearch.action.bulk.BulkItemResponse@13275d8, org.elasticsearch.action.bulk.BulkItemResponse@15b82644, org.elasticsearch.action.bulk.BulkItemResponse@20576557, org.elasticsearch.action.bulk.BulkItemResponse@574cd322, org.elasticsearch.action.bulk.BulkItemResponse@45c2e0a6, org.elasticsearch.action.bulk.BulkItemResponse@119c745c, org.elasticsearch.action.bulk.BulkItemResponse@a7ad6e5, org.elasticsearch.action.bulk.BulkItemResponse@3b1ed14b, org.elasticsearch.action.bulk.BulkItemResponse@690bc15e, org.elasticsearch.action.bulk.BulkItemResponse@1bc776b7, org.elasticsearch.action.bulk.BulkItemResponse@475646d4, org.elasticsearch.action.bulk.BulkItemResponse@a0f53fc, org.elasticsearch.action.bulk.BulkItemResponse@3db972d2, org.elasticsearch.action.bulk.BulkItemResponse@1debc91c, org.elasticsearch.action.bulk.BulkItemResponse@687e4c93, org.elasticsearch.action.bulk.BulkItemResponse@181e72d3, org.elasticsearch.action.bulk.BulkItemResponse@6ec7bce0, org.elasticsearch.action.bulk.BulkItemResponse@2d272b0d, org.elasticsearch.action.bulk.BulkItemResponse@3727f0ee, org.elasticsearch.action.bulk.BulkItemResponse@5c448433, org.elasticsearch.action.bulk.BulkItemResponse@fb713e7, org.elasticsearch.action.bulk.BulkItemResponse@1b5c3e5f, org.elasticsearch.action.bulk.BulkItemResponse@13741d5a, org.elasticsearch.action.bulk.BulkItemResponse@6b69761b, org.elasticsearch.action.bulk.BulkItemResponse@5c7668ba, org.elasticsearch.action.bulk.BulkItemResponse@26221bad, org.elasticsearch.action.bulk.BulkItemResponse@158f4cfe, org.elasticsearch.action.bulk.BulkItemResponse@35f639fa, org.elasticsearch.action.bulk.BulkItemResponse@5aaaa446, org.elasticsearch.action.bulk.BulkItemResponse@6c6333cd, org.elasticsearch.action.bulk.BulkItemResponse@3e47a03, org.elasticsearch.action.bulk.BulkItemResponse@7d9ba6c, org.elasticsearch.action.bulk.BulkItemResponse@8deb645, org.elasticsearch.action.bulk.BulkItemResponse@7dd611c8, org.elasticsearch.action.bulk.BulkItemResponse@5d425813, org.elasticsearch.action.bulk.BulkItemResponse@1702830d, org.elasticsearch.action.bulk.BulkItemResponse@6a937336, org.elasticsearch.action.bulk.BulkItemResponse@278667fd, org.elasticsearch.action.bulk.BulkItemResponse@684b31de, org.elasticsearch.action.bulk.BulkItemResponse@6b52dd31, org.elasticsearch.action.bulk.BulkItemResponse@1a2909ae, org.elasticsearch.action.bulk.BulkItemResponse@e042c99, org.elasticsearch.action.bulk.BulkItemResponse@3f702946, org.elasticsearch.action.bulk.BulkItemResponse@42b6d0cc, org.elasticsearch.action.bulk.BulkItemResponse@1000d54d, org.elasticsearch.action.bulk.BulkItemResponse@3f4f5330, org.elasticsearch.action.bulk.BulkItemResponse@14b7786, org.elasticsearch.action.bulk.BulkItemResponse@750ff7d3, org.elasticsearch.action.bulk.BulkItemResponse@63b3ee82, org.elasticsearch.action.bulk.BulkItemResponse@2620e717, org.elasticsearch.action.bulk.BulkItemResponse@7c8d5312, org.elasticsearch.action.bulk.BulkItemResponse@7636823f, org.elasticsearch.action.bulk.BulkItemResponse@a0db585, org.elasticsearch.action.bulk.BulkItemResponse@2b34e38c, org.elasticsearch.action.bulk.BulkItemResponse@3d37203b, org.elasticsearch.action.bulk.BulkItemResponse@7fd26ad8, org.elasticsearch.action.bulk.BulkItemResponse@1894593a, org.elasticsearch.action.bulk.BulkItemResponse@14b0e127, org.elasticsearch.action.bulk.BulkItemResponse@10823d72, org.elasticsearch.action.bulk.BulkItemResponse@7cea0110, org.elasticsearch.action.bulk.BulkItemResponse@468dda3e, org.elasticsearch.action.bulk.BulkItemResponse@5527b211, org.elasticsearch.action.bulk.BulkItemResponse@54cf7c6a, org.elasticsearch.action.bulk.BulkItemResponse@78010562, org.elasticsearch.action.bulk.BulkItemResponse@50756c76, org.elasticsearch.action.bulk.BulkItemResponse@38aafb53, org.elasticsearch.action.bulk.BulkItemResponse@1729ec00, org.elasticsearch.action.bulk.BulkItemResponse@67f3d192, org.elasticsearch.action.bulk.BulkItemResponse@1c9e07c6, org.elasticsearch.action.bulk.BulkItemResponse@73010765, org.elasticsearch.action.bulk.BulkItemResponse@2b10ace9, org.elasticsearch.action.bulk.BulkItemResponse@52169758, org.elasticsearch.action.bulk.BulkItemResponse@3eda0aeb, org.elasticsearch.action.bulk.BulkItemResponse@459b187a, org.elasticsearch.action.bulk.BulkItemResponse@6b4283c4, org.elasticsearch.action.bulk.BulkItemResponse@d0865a3, org.elasticsearch.action.bulk.BulkItemResponse@636bbbbb, org.elasticsearch.action.bulk.BulkItemResponse@7eae3764, org.elasticsearch.action.bulk.BulkItemResponse@10dc7d6, org.elasticsearch.action.bulk.BulkItemResponse@4f668f29, org.elasticsearch.action.bulk.BulkItemResponse@716e431d, org.elasticsearch.action.bulk.BulkItemResponse@7e744f43, org.elasticsearch.action.bulk.BulkItemResponse@11a8042c, org.elasticsearch.action.bulk.BulkItemResponse@6a4ccef7, org.elasticsearch.action.bulk.BulkItemResponse@69391e08, org.elasticsearch.action.bulk.BulkItemResponse@35eb4a3b, org.elasticsearch.action.bulk.BulkItemResponse@64b3b1ce, org.elasticsearch.action.bulk.BulkItemResponse@6884f0d9, org.elasticsearch.action.bulk.BulkItemResponse@49ec6a9f, org.elasticsearch.action.bulk.BulkItemResponse@26b95b0b, org.elasticsearch.action.bulk.BulkItemResponse@5f7da3d3, org.elasticsearch.action.bulk.BulkItemResponse@103082dd, org.elasticsearch.action.bulk.BulkItemResponse@3a22bad6]
           * response.getTook(): 45ms
           */
          System.out.println("response.getItems(): " + Arrays.toString(response.getItems()));
          System.out.println("response.getTook(): " + response.getTook());
        }

        // после обработки данных коммитим обработанную пачку вручную
        consumer.commitSync();

        if (System.currentTimeMillis() - now > TimeUnit.MINUTES.toMillis(1)) {
          logger.info("Program will be stopped");
          break;
        }

        Thread.sleep(1000);
      }
    }
  }

  /**
   * {
   * "$schema": "/mediawiki/recentchange/1.0.0",
   * "meta": {
   * "uri": "https://www.wikidata.org/wiki/Q13187",
   * "request_id": "b295d2b4-2948-4072-b26b-dd303367b1d1",
   * "id": "87fe17e8-6e9d-406c-b77e-27123bcdceff",
   * "dt": "2023-10-29T10:26:13Z",
   * "domain": "www.wikidata.org",
   * "stream": "mediawiki.recentchange",
   * "topic": "codfw.mediawiki.recentchange",
   * "partition": 0,
   * "offset": 724054332
   * }
   * }
   *
   * @param json string
   * @return extracted value of "87fe17e8-6e9d-406c-b77e-27123bcdceff"
   */
  private static String parseRecordId(String json) {
    return JsonParser.parseString(json)
                     .getAsJsonObject()
                     .get("meta")
                     .getAsJsonObject()
                     .get("id")
                     .getAsString();
  }

}
