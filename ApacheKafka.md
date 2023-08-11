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
- реплики ТОЛЬКО РАДИ репоикации данных (since v2.4+ can read from closest ISR)
