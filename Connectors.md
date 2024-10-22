# Kafka Connector:

## 0. Какие источники и приемники поддерживаются?

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

## 1. Какое API предоставляет система? 

#### Требуется описать основные функциональные возможности (копирование, репликация и тп). Описать основные параметры операций (частота репликации и тп). Приложить ссылки на документацию и описание функционала.

Каждый коннектор имеет свои уникальные параметры настройки, такие как конфигурация источника данных (например, настройки подключения к базе данных) или целевой системы (например, настройки конечной точки для данных)

**API:**

- Коннекторы и их конфигурация: Конфигурация каждого коннектора подробно описана в документации. Например, JDBC Source Connector Configuration для импорта данных из реляционных баз данных.
- REST API для управления коннекторами: Kafka Connect REST API позволяет управлять состоянием и конфигурациями коннекторов, их задач и потоками.

**Частота репликации:**

В Kafka Connect понятие "частота репликации" напрямую не используется. Однако, если речь идет о частоте, это зависит от типа коннектора и его конфигурации.

**Частота репликации для источников данных (source connectors):**
- Коннекторы для источников, такие как JDBC Source Connector (для баз данных). Параметр `poll.interval.ms` определяет, как часто коннектор будет опрашивать источник данных на наличие новых данных. Значение по умолчанию для этого параметра — 5000 миллисекунд (5 секунд).

**Частота репликации для целевых систем (sink connectors):**
- Коннекторы для конечных систем (sink connectors), работают в режиме "стриминга", забирая данные из Kafka и записывая их в целевую систему по мере их поступления. Здесь частота репликации больше зависит от скорости обработки данных и от параметров производительности, чем от настраиваемого интервала. Параметры, влияющие на частоту передачи данных:
- `poll.interval.ms`
- `batch.size`: Параметр применим как для source, так и для sink коннекторов. (размер партии данных для чтения или записи)

_Если речь идет о фактической репликации сообщений в кластере Kafka (между брокерами), то это регулируется настройками самого Kafka, а не Kafka Connect, (replication.factor, min.insync.replicas)._

## 4. Как решается технологическая задача? 
### Используются ли триггеры, журналы транзакций, лог-снифинг, или иные подходы?

Kafka Connect поддерживает несколько методов интеграции данных в зависимости от особенностей источника:

- CDC (лог-снифинг) с использованием журналов транзакций — самый популярный. Отслеживание всех изменений в базе данных с минимальным вмешательством в работу приложения. Реальная синхронизация данных, практически в режиме реального времени.
- Триггеры — не всегда оптимальны для производительности. Для захвата изменений в БД на уровне SQL. Снижают скорость обработки транзакций. Используют если БД не поддерживает CDC или журналы транзакций.
- Polling (опрос) — неэффективен для больших данных, используется, когда подходы выше невозможны. Есть вероятность потери данных.
- API и событийная модель — хороший вариант для интеграции с системами, использующими REST API или стриминг.
  
## 6. Дизайн системы. 

#### Описанием системы, её ключевых компонентов. Какая у каждого компонента ответственность, и как они взаимодействуют друг с другом?

зачем придумали https://www.confluent.io/blog/event-streaming-platform-1/

- Коннекторы : абстракция высокого уровня, которая координирует потоковую передачу данных путем управления задачами.
- Задачи : Реализация копирования данных в Kafka и из нее.
- Рабочие : запущенные процессы, которые выполняют коннекторы и задачи.
- Конвертеры : код, используемый для преобразования данных между Connect и системой, отправляющей или получающей данные.
- Преобразования : простая логика для изменения каждого сообщения, созданного или отправленного коннектору.
- Очередь мертвых писем : как Connect обрабатывает ошибки коннектора

[Источник данных] -> [Source Connector] -> [Kafka] -> [Sink Connector] -> [Целевая система]

**Основные компоненты Kafka Connect:**
- Коннекторы (Connectors) реализует логику подключения к внешней системе. Существуют два типа коннекторов:
-- **Source Connectors:** Коннекторы источников данных. Они извлекают данные из внешних систем и записывают их в топики Kafka.
-- **Sink Connectors:** Коннекторы назначения. Они забирают данные из топиков Kafka и отправляют их во внешние системы.

- Рабочие узлы (Workers) запускают коннекторы и распределяют задачи между собой в зависимости от режима работы. Рабочие узлы могут быть запущены в двух режимах:
  - **Standalone Mode:** коннекторы и задачи запускаются в одном рабочем узле.
  - **Distributed Mode:** Несколько рабочих узлов совместно обрабатывают задания, что обеспечивает масштабируемость и отказоустойчивость.

- **Задачи (Tasks)** отдельные процессы, выполняют работу коннекторов. Каждый коннектор может быть разбит на несколько задач для параллельной обработки. Если источник данных имеет множество партиций, каждая задача может работать с отдельной партицией.

- **Конфигурации коннекторов** определяет, как должен работать коннектор. Конфигурация может включать параметры подключения к БД, частоту опроса данных, схему данных, настройки безопасности и другие важные параметры. передаются при создании коннектора. Kafka Connect автоматически применяет конфигурации к рабочим узлам и задачам.
  
- **Offset Management** (Управление смещениями) автоматически управляет смещениями (offsets) для каждого коннектора и задачи. Offset Management регулирует, где процесс чтения или записи данных остановился, чтобы в случае перезапуска система могла продолжить с того же места.

- **Топики Kafka для хранения метаданных** В распределенном режиме Kafka Connect использует несколько системных топиков Kafka для управления метаданными и координацией работы:
 - `connect-configs`
 - `connect-offsets`
 - `connect-statuses`

- **REST API** интерфейс для управления коннекторами, рабочими узлами и задачами. Через API можно запускать коннекторы, обновлять их конфигурации, проверять состояние и получать статистику. Для управления и мониторинга системы.

#### Взаимодействие между компонентами:
- Коннекторы извлекают или записывают данные. Коннекторы могут быть настроены на параллельную работу, создавая несколько задач, которые выполняются на рабочих узлах.
- Рабочие узлы взаимодействуют с топиками Kafka, передавая данные в Kafka или получая данные из Kafka для их последующей передачи в целевую систему.
- Offset Management контролирует прогресс выполнения задач, чтобы избежать дублирования данных и потери прогресса при сбоях.
- REST API предоставляет интерфейс для конфигурации коннекторов, мониторинга и управления ими.
- Топики Kafka для хранения метаданных используются в распределенном режиме для координации работы коннекторов, обеспечения согласованности и отказоустойчивости.

Подробнее:
- https://docs.confluent.io/platform/current/connect/design.html
- https://docs.confluent.io/platform/current/connect/index.html#what-is-kafka-connect

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
