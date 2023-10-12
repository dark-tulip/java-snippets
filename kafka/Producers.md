## Producers
- записывают данные в партицию топика
- одна партиция может читать данные из нескольких продюсеров
- продюсеру можно назначить message-key (определяет в какую партицию попадут данные)
- сообщения с одним и тем же message-key запишутся в одну и ту же партицию (hashing)
- null message key means round-robin between partitions
- выбирает партицию куда записывать
- отвечает за гарантию доставки
- тюнинг производительности (отправлять batch-ами, по времени, по размеру, по кол-ву сообщений, использоватние сжатия*)
- сжатие только для больших данных, есть весь CPU

## Kafka message consists of
- msg key (binary)
- value (binary)
- compression type
- headers (optional)
- partition + offset
- timestamp

#### Формат передачи данных из кафки
- accepts ONLY BYTES as an input, and sends bytes as an output to consumers
- `kafka message serializer` - собирает кафка мэссейджы затем преобразует KEY и VALUE сообщения в байтовый формат
- serialization means transform data into bytes

### Kafka message key hashing
- каждое сообщение чиатется **Producer Partitioner Logic**, которая решает в какую партицию попадет сообщение
- `record` -> `.send()` -> `Kafka Partitioner` -> `.assignPartition()` -> `Partition`
- default key hashing algorithm is murmur2
  ` targetPartition = Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1)`

## Producer Acknowledgements (acks) and durability
Гарантии доставки событий продюсеру
- `acks=0` - won't wait acknowledgement (possible data loss) - metrics, logging
- `acks=1` - leader acknowledgement (limited data loss) - sends response for each successful write request - NO guarantee that data replicated
- `acks=all` - leader + replicas acknowledgement (no data loss) or (`acks=-1`) - the safest data guarantee (accepted by all ISR)
- имея фактор репликации в N, имея выбивших из строя N-1 брокера, можно восстановить данные

## Мин кол-во ISR реплик при `acks=all`
- `min.insync.replicas` - `acks=all` goes hand by hand with this setting
- `min.insync.replicas=1` - only leader accepted the data
- `min.insync.replicas=2` - one leader and one replica accepted the data
```C#
enum Acks {
 None = 0,
 Leader = 1,
 All=-1
}
```
если будем ждать подтверждения от всех реплик, запись застрянет, to avoid use:
(PS сколько готовы потерять брокеров, и продолжить работу)
```
Topic config: min.insync.replicas = 2
```

#### Самое устойчивое (popular)
- иметь три брокера
- фактор репликации `min.insync.replicas=2` (можем потерять 1 ISR без боли)
- уровень согласованности `acks=all`
- !!! если изменить только `acks=all` чревато NOT ENOUGH REPLICA EXCEPTION (когда одна из реплик упадет)
### Java producer

### Размеры сообщений, смотрите на согласованность
```
BROKER   `message.max.bytes`         (1_000_012)
PRODUCER `request.max.size`          (1_048_576)
CONSUMER `max.partition.fetch.bytes` (1_048_576)
```
- batch.size - набирает пачку в 16КВ

### Блокирующий вызов send
- у кафки крутой синхронный API, если метаданные с кластера блокируются, зависает - до 60 сек
- если метаданные не доступны, producer.send() блокируется
- max.block.ms = 60_000

### Worker thread
```Java
producer.send(
  new ProducerRecord<>(topic, partition, key, value), callback);
)
```
`max.block.ms` - сколько времени может занять send (DEFAULT 60 sec)
- -> `Metadata` - куда должно попасть сообщение, which broker
- -> `Serializer` - сериализация ключа и сообщения
- -> `Partitioner` - в какую партицию попадет partitioner.class
  => RecordAccumulator - сбор пачки данных и этап компрессии `[-> Compressor]` (here `batch.size`)


### Sender thread
- Sender Thread - выбирает какая партиция попадет на какого брокера (drain batches - сливать порции данных)
- `linger.ms` - забирать пачку при превышении таймаута
- `acks` - уровень гарантии доставки данных
```
Drain batches (слить батчи) -> Make requests (составить запросы) -> Poll connections (стянуть соединения) -> Fire callbacks (запустить колбэки)
```

### Network thread
- send bathces to brokers in cluster
- throughput (bps) - bytes per second
- throughput (rps) - requests per second (record-send-rate)

### Total latency = worker latency + sender latency + callback latency

- `worker latency` - время выполнения producer.send()
- `sender latency` - время до получения ответа от брокера (внутренний код кафки)
- `callback latency` - не влияет на e2e latency

## Create producer

- когда забыли указать параметры сериализации сообщений для продюсера
- `KEY_SERIALIZER_CLASS_CONFIG`
- `VALUE_SERIALIZER_CLASS_CONFIG`

```
Exception in thread "main" org.apache.kafka.common.config.ConfigException: 
Invalid value null for configuration key.serializer: must be non-null.
```

- `linger.ms` - batching by TIME. таймаут отправки батча (если заполнится, отправится раньше)
- `batch.size` - batching by SIZE. default 16384 bytes (16KB)
- `retries` - по умолчанию INTEGER.MAX_VALUE
- `max.block.ms` - cколько максимально времени дается команде producer.send() - default 1 min
- CompressionType - none is by default
- `acks` - запрос не считается завершенным пока не сработает подтверждение заданного уровня
  --`0` - no any ack
  -- `1` оnly leader, without acknowledgement on followers
  -- `-1` or `all` - default

Callbacks guarantee execution order
- все вызовы producer.send() являются асинхронными и возвращают java.concurrent.Future<RecordMetadata>,
- вывоз метода get() сделает его блокирующим вызовом (из асинхронного синхронный код)

## Partitioner classes
- option default `partitioner.class = null`
- you can implement `Partitioner interface`
- StickyPartitioner - batch of messages to one partition
- RoundRobinPartitioner - one message to one partition like RR

### Как выбирается стратегия?
- используйте стратегию по умолчанию (удалите `partitioner.class`, реализация была перемещена внутри )
- partition number on ProducerRecord
- message key hash if exists
- if no key, no partitioning class specified, use Sticky partitioner, which changes when the `buffer.size` is full
- RoundRobinPartitioner не зависит от message key

```bash
tansh@MBP-tansh ~ % kafka-console-consumer --bootstrap-server localhost:9092 --topic topic1 --property print.key=true --property print.partition=true --property print.value=true
Partition:0	null	value: 0
Partition:1	null	value: 1
Partition:2	null	value: 2
Partition:0	null	value: 3
Partition:1	null	value: 4
Partition:2	null	value: 5
Partition:0	null	value: 6
Partition:1	null	value: 7
Partition:2	null	value: 8
Partition:0	null	value: 9
```
- свойство `partitioner.class` больше НЕ рекомендуется к использованию
- `delivery.timeout.ms` should be grater than sum of `linger.ms + request.timeout.ms`
- with default partitioner (`DefautPartitioner - delete partitioner.class`):
  - if partition specified in record - use it
  - if key specified same record always goes to the same partition
  - if key not specified, changes when `batch.size` is full
- `UniformStickyPartitioner` deprecated because
  - has no guarantee that record with same key goes to the same partition
- в новой реализации
- выбором партиции занимается ReccordAccumulator когда создает новый батч для партиции топика
- до создания батча исполняется метод onNewBatch, который решает в какую партицию отправится батч
- производительность по null key, user defined partition number не изменится
```Java
/**
 * Executes right before a new batch will be created. For example, if a sticky partitioner is used,
 * this method can change the chosen sticky partition for the new batch.
 * @param topic The topic name
 * @param cluster The current cluster metadata
 * @param prevPartition The partition of the batch that was just completed
 */
default public void onNewBatch(String topic, Cluster cluster, int prevPartition) {
}
```
## Partitioning examples

### Key specified
```bash
Partition:2	key: 0	value: 0
Partition:1	key: 1	value: 1
Partition:1	key: 2	value: 2
Partition:2	key: 3	value: 3
Partition:2	key: 4	value: 4
Partition:2	key: 5	value: 5
Partition:0	key: 6	value: 6
Partition:0	key: 7	value: 7
Partition:2	key: 8	value: 8
Partition:1	key: 9	value: 9
Partition:2	key: 0	value: 0
Partition:1	key: 1	value: 1
Partition:1	key: 2	value: 2
Partition:2	key: 3	value: 3
Partition:2	key: 4	value: 4
Partition:2	key: 5	value: 5
Partition:0	key: 6	value: 6
Partition:0	key: 7	value: 7
Partition:2	key: 8	value: 8
Partition:1	key: 9	value: 9
```
### Key not specified
`properties.setProperty("batch.size", "128");`

```bash
Partition:0	null	value: 0
Partition:0	null	value: 1
Partition:2	null	value: 2
Partition:2	null	value: 3
Partition:1	null	value: 4
Partition:1	null	value: 5
Partition:0	null	value: 6
Partition:0	null	value: 7
Partition:0	null	value: 8
Partition:0	null	value: 9
Partition:0	null	value: 0
Partition:0	null	value: 1
Partition:0	null	value: 2
Partition:0	null	value: 3
Partition:1	null	value: 4
Partition:1	null	value: 5
Partition:2	null	value: 6
Partition:2	null	value: 7
Partition:1	null	value: 8
Partition:1	null	value: 9
```
## RoundRobin
```bash
Partition:0	key: 0	value: 0
Partition:1	key: 1	value: 1
Partition:2	key: 2	value: 2
Partition:0	key: 3	value: 3
Partition:1	key: 4	value: 4
Partition:2	key: 5	value: 5
Partition:0	key: 6	value: 6
Partition:1	key: 7	value: 7
Partition:2	key: 8	value: 8
Partition:0	key: 9	value: 9
```
## UniformStickyPartitioner (Deprecated) - cпецификация по ключу не гарантирована
```bash
Partition:0	key: 0	value: 0
Partition:2	key: 1	value: 1
Partition:0	key: 2	value: 2
Partition:1	key: 3	value: 3
Partition:0	key: 4	value: 4
Partition:1	key: 5	value: 5
Partition:2	key: 6	value: 6
Partition:1	key: 7	value: 7
Partition:0	key: 8	value: 8
Partition:2	key: 9	value: 9
Partition:0	key: 0	value: 0
Partition:2	key: 1	value: 1
Partition:0	key: 2	value: 2
Partition:2	key: 3	value: 3
Partition:0	key: 4	value: 4
Partition:2	key: 5	value: 5
Partition:0	key: 6	value: 6
Partition:1	key: 7	value: 7
Partition:0	key: 8	value: 8
Partition:1	key: 9	value: 9
```


## Producer retries (обработка ошибок на стороне разработчика)
- чтобы избежать транзиентных ошибок
- в кафке до 2.0 deefault 0 retries
- Integer.MAX_VALUE (2147483647) for kafka >= 2.1
- `retry.backoff.ms=100` by default RETRY_BACKOFF_MS_CONFIG - how much time wait before the next retry
- `delivery.timeout.ms` определяет через какое время сообщение считается не отправленным в случае сбоя (default 120sec)
#### `delivery.timeout.ms>= linger.ms + retry.backoff.ms + request.timeout.ms`
-> `send()` -> `batching` -> `await send()` -> `retries` -> `inflight`

- из-за retries сообщения могут записываться вне порядка (out of order) 
- `max.in.flight.requests.per.connection = 5` - максимальное кол-во запросов, которые могут быть отправлены клиенту без подтверждения
- `enable.idempotence = true` and `max.in.flight.requests.per.connection = 1` для устранения дубликатов, и гарантии порядка доставки сообщений 

#### Идемпотентный продюсер (по умолчанию с kafka 3.0)
- !!!idempotent producer don't will commit twice due to network issues


# General producer configuration !!! Pay attention for this configs
## Особенно если версия кафки <= 2.8!!!
- acks=all (to work with min.insync.replicas)
- min.insync.replicas=2 (stable guarantee of data storing)
- enable.idempotency=true (to avoid duplicates due to network issues)
- max.inflight.requests.per.connection=5 (keep msg ordering and max performance)
- retries=Integer.MAX_INT (повторять до истечения `delivery.timeout.ms`)
- delivery.timeout.ms=120000 (fail after retrying for 2 min-s)

```log
Properties properties = new Properties();
properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

// for safe producer configs (kafka <= 2.8)
properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE + "");
properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
properties.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
```


### Message compression
- применимо когда большой размер сообщения
can be applied on:
- (1) broker level (to all topics)
- (2) topic level
- компрессия идет на уровне пачки (все сообщения из одной пачки, к примеру можно до 4х раз уменьшить) 
- уменьшение нагрузки на сеть
- выше пропускная способность
- меньше размер хранения на диске
- МИНУС нагрузка на CPU для продюсеров
- МИНУС нагрузка на CPU для консюмеров
- `compression.type=producer` - broker takes compressed batch from prodecer and writes direct to the topics log without recompression
- `compression.type=none` - all batches decompressed on broker
- !!! если типы компрессии различаются на продюсере и на брокере, данные при сохранении в диск будут декомпрессироваться и обратно сжиматься
- if broker/topic level compression matches producers' setting, data will be stored as is
- if different compression setting - batches will be decompressed by the broker and recompressed again using specified algorithm


## Механизм батчинга
- `linger.ms` (default 0) - how long to wait until we send a batch (сколько времени ожидать сборки пачки)
- `batch.size` - *максимальный размер пачки в байтах* увеличьте, если батч переполнился до истечения `linger.ms`
- по умолчанию кафка продюсеры отправляют сообщения ASAP (сразу же, while linger.ms=0)
- - kafka is smart, пока есть `max.inflight.requests.per.connection=5` сообщений на лету, копится следующая пачка
- ONE BATCH = ONE REQUEST (max.size is batch size)
- batch.size=16 KB default, (may set 32KB or 64 KB)
- если сообщение больше batch.size оно сразу же отправится
- batch is allocated per partition (each batch goes to one partition)

### для увеличения пропуской способности (! но пожертвовав cpu и задержкой linger.ms)
```log
properties.setProperty("batch.size", Integer.toString(64 * 1024));
properties.setProperty("linger.ms", "20");
properties.setProperty("compression.type", "snappy");
```
