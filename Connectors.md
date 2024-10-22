# Kafka Connector:

### 0. Какие источники и приемники поддерживаются?
```
ActiveMQ Sink Connector
ActiveMQ Source Connector
Amazon CloudWatch Logs Source Connector
Amazon CloudWatch Metrics Sink Connector
Amazon Kinesis Source Connector
Amazon S3 Sink Connector
Amazon S3 Source Connector
Amazon SQS Source Connector
Apache HBase Sink Connector
Appdynamics Metrics Sink Connector
AWS DynamoDB Sink Connector
AWS Lambda Sink Connector
AWS Redshift Sink Connector
Azure Blob Storage Sink Connector
Azure Blob Storage Source Connector
Azure Cognitive Search Sink Connector
Azure Data Lake Storage Gen1 Sink Connector
Azure Data Lake Storage Gen2 Sink Connector
Azure Event Hubs Source Connector
Azure Functions Sink Connector
Azure Service Bus Source Connector
Azure Synapse Analytics Sink Connector
Cassandra Sink Connector
Databricks Delta Lake Sink Connector
Data Diode Connector (Source and Sink)
Datadog Metrics Sink Connector
Datagen Source Connector (Development Testing)
Elasticsearch Sink Connector
GitHub Source Connector
Google BigQuery Sink Connector
Google Cloud BigTable Sink Connector
Google Cloud Dataproc Sink Connector
Google Cloud Functions Sink Connector
Google Cloud Pub/Sub Source Connector
Google Cloud Spanner Sink Connector
Google Cloud Storage Sink Connector
Google Cloud Storage Source Connector
Google Firebase Connector
HEAVY-AI (formerly OmniSci) Sink Connector
HDFS 2 Sink Connector
HDFS 2 Source Connector
HDFS 3 Sink Connector
HDFS 3 Source Connector
HTTP Sink Connector
HTTP Source Connector
IBM MQ Source Connector
IBM MQ Sink Connector
InfluxDB Connector (Source and Sink)
JDBC Connector (Source and Sink)
JMS Sink Connector
JMS Source Connector
Kudu Connector (Source and Sink)
MapR DB Sink Connector
Microsoft SQL Server Source Connector (Deprecated)
MongoDB Source Connector (Debezium)
MQTT Connector (Source and Sink)
MySQL Source Connector (Debezium)
Netezza Sink Connector
Oracle CDC Source Connector
Pagerduty Sink Connector
PostgresSQL Source Connector (Debezium)
RabbitMQ Sink Connector
RabbitMQ Source Connector
Redis Sink Connector
Salesforce Bulk API Source Connector
Salesforce Change Data Capture Source Connector
Salesforce Connector (Source and Sink)
ServiceNow Connector (Source and Sink)
SFTP Connector (Source and Sink)
SNMP Trap Source Connector
Solace (Source and Sink)
Splunk Sink Connector
Splunk Source Connector
Splunk S2S Source Connector
Spool Dir Connector
SQL Server Source Connector (Debezium)
Syslog Source Connector
Teradata Connector (Source and Sink)
TIBCO Sink Connector
TIBCO Source Connector
Vertica Sink Connector
VMware Tanzu Gemfire Sink Connector
Zendesk Source Connector
```

### 1. Какое API предоставляет система? Требуется описать основные функциональные возможности (копирование, репликация и тп). Описать основные параметры операций (частота репликации и тп). Приложить ссылки на документацию и описание функционала.

Конфигурация коннекторов: Каждый коннектор имеет свои уникальные параметры настройки, такие как конфигурация источника данных (например, настройки подключения к базе данных) или целевой системы (например, настройки конечной точки для данных).

**API:**

- Коннекторы и их конфигурация: Конфигурация каждого коннектора подробно описана в документации. Например, JDBC Source Connector Configuration для импорта данных из реляционных баз данных.
- REST API для управления коннекторами: Kafka Connect REST API позволяет управлять состоянием и конфигурациями коннекторов, их задач и потоками.

**Частота репликации:**

В Kafka Connect понятие "частота репликации" напрямую не используется. Однако, если речь идет о частоте, это зависит от типа коннектора и его конфигурации.

**Частота репликации для источников данных (source connectors):**
- Коннекторы для источников, такие как JDBC Source Connector (для баз данных). Параметр `poll.interval.ms` определяет, как часто коннектор будет опрашивать источник данных на наличие новых данных. Значение по умолчанию для этого параметра — 5000 миллисекунд (5 секунд).

**Частота репликации для целевых систем (sink connectors):**
- Коннекторы для конечных систем (sink connectors), работают в режиме "стриминга", забирая данные из Kafka и записывая их в целевую систему по мере их поступления. Здесь частота репликации больше зависит от скорости обработки данных и от параметров производительности, чем от настраиваемого интервала. Параметры, влияющие на частоту передачи данных:
- `poll.interval.ms.
- `batch.size`: Параметр применим как для source, так и для sink коннекторов. (размер партии данных для чтения или записи)

! Если речь идет о фактической репликации сообщений в кластере Kafka (между брокерами), то это регулируется настройками самого Kafka, а не Kafka Connect, (replication.factor, min.insync.replicas).

4. Как решается технологическая задача? Используются ли триггеры, журналы транзакций, лог-снифинг, или иные подходы?
  
6. Дизайн системы. Описанием системы, её ключевых компонентов. Какая у каждого компонента ответственность, и как они взаимодействуют друг с другом?

Подробнее:
https://docs.confluent.io/platform/current/connect/design.html

8. Какие форматы данных используются для передачи изменений? Есть ли поддержка гибридных форматов или сжатия данных? 
Почему выбран тот или иной формат?
9. Технологический стек компонентов. Какие языки программирования и платформы используются для ключевых компонентов системы? 
Были ли особенности выбора этих технологий (производительность, совместимость, масштабируемость)?
10. Масштабируемость и производительность. Как система справляется с большими объемами данных? 
Какие механизмы для горизонтального и вертикального масштабирования предусмотрены? 
В каких случаях система начинает плохо справляться и почему?
11. Как технологически система справляется с изменениями в схеме данных (например, добавление/удаление полей)?
12. Есть ли возможность пользователям самим добавлять источники и таргеты, как реализована эта функциональность? 
Ответ на это вопрос может помочь нам начать масштабироваться по источникам и таргетам.
13. Вопрос на будущее. Как встраивается обработка потока данных, аналитика над ним? 
Какие технологии для обработки и аналитики доступны?
