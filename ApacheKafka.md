## Kafka
- distributed streaming system
- horizontal scaling via brokers
- позволяет уменьшить количество интеграций
- успешна на real time переносах данных

## Kafka Topics
- непрерывный поток данных
- как таблица в БД, но без constraint-ов, и тут нельзя делать запросы
- у каждого топика есть имя, по которому топик идентифицируется
- топик кафки `IMMUTABLE` - нельзя изменить или удалить данные с партиции, можно только дозаписывать
- чтобы записать данные в топик - нужно знать адрес любого брокера и название топика
- to read data from topic you need any broker and topic name

## Partitions and offsets
- каждый топик разделен на **партиции**, партиция которая содрержит message-ы в упорядоченном формате
- каждое сообщение содержит инкрементальный id в партиции, который называется **Kafka partition offset**-ом
- данные хранятся ограниченное время (default 1 week)
- offset-ы относятся ТОЛЬКО к ПАРТИЦИЯМ, у каждой партиции свой оффсет
- оффсеты НЕ переиспользуются, даже если данные были удалены
- порядок оффсета обеспечивается в каждой партиции отдельно, данные между партициями НЕ пересекаются
- **LEADER** and **ISR**

## Producers
- записывают данные в партицию топика
- одна партиция может читать данные из нескольких продюсеров
- продюсеру можно назначить message-key (определяет в какую партицию попадут данные)
- сообщения с одним и тем же message-key запишутся в одну и ту же партицию (hashing)
- null message key means round-robin between partitions

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

## Consumers
- Кафка брокер НЕ кидает в консюмеры,
- консюмеры читают из брокера
- consumers request data from kafka brokers (servers) - PULL MODEL
- value and key of the message deserializes (from bytes into objects) by `Consumer Deserializer`
- если вы хотите изменить формат сериализации или десериализации - СОЗДАЙТЕ НОВЫЙ ТОПИК
### Consumer groups
- консюмеры могут читать группой из одного и того же топика
- кол-во консюмеров может быть больше числа партиций - they will be inactive
- из одного топика могут читать несколько консюмер-групп НО из каждой консюмер-группы каждую партицию в топике читает ТОЛЬКО ОДИН консюмер
- чтобы добавить консюмер в группу use `group.id` property to consumer
  
### Delivery semantics
**at least once**
- консюмеры коммитят каждое чтение один раз
- если данные потерялись, read again
  
**at most once**
- offset комитися как только была прочтена запись
- если прошла ошибка, данные могут потеряться
  
**exactly once**
- kafka streaming API
- idempotency

## Kafka brokers
- кластер кафки состоит из нескольких брокеров (серверов)
- каждый брокер идентифицируется ID
- каждый брокер читает данные из партиций
- подсоединившись к одному bootstrap брокеру вы имеете доступ ко всему кластеру
- партиции одного топика могут расоплогаться на разных брокерах, так данные распредленяются партициями между брокерами
- каждый кафка брокер является бутсрап сервером, благодаря которому вы имеете доступ ко всему кластеру
- каждый брокер знает все об остальных брокерах (партиции, топики и другие метаданные)
  
## topic replication factor
- ONLY ONE BROKER can be leader of partition
- producers send data only for leader broker
- все реплики называются ISR - **IN SYNC REPLICA**
- у каждой партиции может быть только один ЛИДЕР, в которую записыввают и по умолчанию читают данные
- реплики ТОЛЬКО РАДИ репоикации данных (since v2.4+ can read from closest ISR - why? - network cost and latency)

## Producer Acknowledgements (acks) and durability
- acks=0 - won't wait acknowledgement (possible data loss)
- acks=1 - leader acknowledgement (limited data loss)
- acks=all - leader + replicas acknowledgement (no data loss)
- имея фактор репликации в N, имея выбивших из строя N-1 брокера, можно восстановить данные

## Zookeeper - первое хранилище метаданных (leaders, partitions)
- используется для упраления брокерами кафки, точнее хранит их список
- zookeeper used to manage kafka brokers
- helps to perform leader elections
- отправляет уведомления кафке об изменениях в конфигурации
- зукипер работает с нечетным числом серверов (1, 3, 5, 7)
- kafka 2.* cannot work without zookeeper (goes together)
- зукипер существует с момента появления кафки
- kafka 3.* may have kafka Raft instead of zookeeper (KIP-500)
- до кафки 4.0 желательно всегда использовать зукипер
- ЗУКИПЕР НИКОГДА НЕ хранит consumer offsets (ложная инфа в интернете)
- zeekeeper leader server (write), others, followers (for read)

### Зачем и когда нужен зукипер?
- когда работают внутренне с брокерами кафки
- не нужен клиентам кафки (по соображениям безопасности)
- весь API и CLI для работы с клиентами кафки уже мигрировал с зукипера в брокеры кафки
- порты зукпера должны быть открыты только кафка брокерам, НЕ кафка клиентам
- НИКОГДА не используйте зукипер в качестве конфигов для кафка клиентов, и других программ, которые connected с кафкой

### KRAFT - Raft протокол пришедший на замену зукиперу
- зукипер страдает по производительности когда кластер кафки имеет более 100_000 партиций
- единая модель безопасности
- слияние с кафкой изнутри
- быстрее время отклика, recovery и shutdown time (planned and unplanned)
- production ready since 3.3.1+
- kafka 4.+ больше не поддерживает зукипер
- проект KIP-500 проект по удалению зукипера из кафки
- to deploy more partitions per cluster

Выборы лидера zK основаны на протоколе, похожем на Paxos, который называется ZAB . Каждая запись проходит через лидера, а лидер генерирует идентификатор транзакции (zxid) и присваивает его каждому запросу на запись. Идентификатор представляет порядок, в котором записи применяются ко всем репликам. Запись считается успешной, если лидер получает подтверждение от большинства. Пояснение к ЗАБ .

## Quorum controller
- event driver architecture
- run in kafka cluster itself
- хранит свое событийное состояние так, что событийная модель  может быть воссоздана
- **events log stores this state**
- другие контроллеры читают события leader quorum controller-a, и сохраняют в своем журнале
- так пропущенные события могут легко восстановиться по журанлу событий
- **metadata топик** is events store


## Install kafka on Mac M1

```bash
## Install via brew
brew install kafka

## Kafka configs
cd /opt/homebrew/etc/kafka

## Zookeeper configs
cd /opt/homebrew/etc/kafka

## Location for kafka data
cd /opt/homebrew/var/log/kafka
```

## Kafka with Zookeeper server start
start zookeeper first
```bash
zookeeper-server-start /opt/homebrew/etc/zookeeper/zoo.cfg
```
then start kafka
```bash
kafka-server-start /opt/homebrew/etc/kafka/server.properties
```
## Start Kafka in KRaft mode
- generate id for cluster

```bash
kafka-storage random-uuid
```

- location of kraft server properties
```bash
/opt/homebrew/etc/kafka/kraft/server.properties
```

- format log dir and replace in the config/kraft/server.properties file, default /tmp/kraft/combined-logs
```bash
kafka-storage format -t 1l18DkhPTemKEvKEcwYEbA -c /opt/homebrew/etc/kafka/kraft/server.properties
# Formatting /opt/homebrew/var/lib/kraft-combined-logs with metadata.version 3.4-IV0.
```

- если прочитать содержимое:
```
...
# A comma separated list of directories under which to store log files
# log.dirs=/opt/homebrew/var/lib/kraft-combined-logs
...
```

start kafka with kraft properties
```bash
kafka-server-start /opt/homebrew/etc/kafka/kraft/server.properties 
```
## Kafka topics CLI

```
kafka-topics playground.config --bootstrap-server cluster.playground.cdkt.io:9092
```

### Java
### Worker thread
```Java
producer.send(
  new ProducerRecord<>(topic, partition, key, value), callback);
)
```
`max.block.ms` - сколько времени может занять send (DEFAULT 60 sec)
-> `Metadata` - куда должно попасть сообщение, which broker
-> `Serializer` - сериализация ключа и сообщения
-> `Partitioner` - в какую партицию попадет partitioner.class
=> RecordAccumulator - сбор пачки данных и этап компрессии `[-> Compressor]` (here `batch.size`)


### Sender thread

-> Sender Thread - выбирает какая партиция попадет на какого брокера (drain batches - сливать порции данных)
`linger.ms` - забирать пачку при превышении таймаута
`acks` - уровень гарантии доставки данных 

```
Drain batches (слить батчи) -> Make requests (составить запросы) -> Poll connections (стянуть соединения) -> Fire callbacks (запустить колбэки)
```

### Network thread
- send bathces to brokers in cluster
