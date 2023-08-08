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
- default key hashing algorythm is murmur2
` targetPartition = Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1)`
