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

## Consumer
- consumer polls (Duration timeout)

### `auto.offset.reset`
- `none` - if we don't have consumer group, we will fail before starting the application
- `earliest` - start from the beginning of the topic
- `latest` - read from the latest offset
при след запуске для одной и той же группы, опция earliest будет читать с последнего offset-a

### ConsumerCoordinator
- Discovered group coordinator
- (Re-)joining group
- assign memberId
- assign partitions for member
- setting offsets for all partitions in topic
consumer is joining a group

- consumers are not thread safe
- to close it from another thread call consumer.wakeup()
- консюмер может оставаться в группе пока он
  - sends heartbeat (периодически отправляет на сервер, its liveness when `session.timeout.ms`)
  - polls records (`max.poll.interval.ms`)

### WARNING - закрытие консюмера
- вызывайте `consumer.wakeup()` из потока закрывающего консюмер (прерывает поллинг), 
- поллинг консюмера в try-catch блок, который обрабатывает WakeupException
- в finally секции `consumer.close()`

### Why do we need gracefully shutting down the consumer with `consumer.close()` methosd?
- to close sockets and network connections
- to gracefully rebalance consumers, before group coordinator discover that consumer was lost

при поднятии нового консюмера той же группы - группа перераспределит партиции между собой
- `Adding newly assigned partitions: topic1-2`
- когда партиций меньше чем консюмеров в группе - они будут пустовать
