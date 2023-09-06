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


- throughput (bps) - bytes per second
- throughput (rps) - requests per second (record-send-rate)

### Total latency = worker latency + sender latency + callback latency

worker latency - время выполнения producer.send()
sender latency - время до получения ответа от брокера (внутренний код кафки)
callback latency - не влияет на e2e latency 

# Kafka topics and partitions

- партиции хранятся на диске;
- партиции разделены на сегменты;
- партиции как единицы параллелизации хранения данных в топике;
- данные можно удалять только целыми сегментами;
- данные удаляются целыми сегментами либо по времени, либо по размеру;
- `retention.bytes=-1` (размер партиции ограничем размером диска)
- `retention.ms=604800000` - 1 неделя (данные хранятся в партиции)
- для каждого сегмента на диске хранится три файла `segment{basic_offset, data, index, timeindex}`
```
basic_offset is 0000123456789 (segment name)
data is         0000123456789.log (append only подход записи)
index is        0000123456789.index (чтобы быстро искать по offset)
timeindex is    0000123456789.timeindex (у каждого события есть время)
```
- сообщение можно быстро найти по его offset-у

# Kafka brokers
- контроллер координирует работу кластера
- один из брокеров в кластере выбирается контроллером
- при replication_factor=3 каждая партиция будет продублирована на одном из брокеров (если брокеров 3)
- чтобы писать данные в партиции, нужно выбрать leader брокера для партиции
  - например, broker 1 is leader for partition 0
  - broker 2 is leader for partition 1
  - each partition should have leader broker
  - репликация с лидера на другие брокеры (**followers**)
  - `ISR` - in sync replica, реплика синхронизированная с лидером
- когда падает лидер, лидером становится один из ISR реплик, когда упавший лидер входит в строй, он догоняет (восстанавливает данные) с нового лидера
- когда в одном брокере больше лидеров чем на других, кафка делает перебалансировку лидера на другой брокер
- у каждой партиции есть свой лидер
- сообщения пишутся в лидер
- данные реплицируются между брокерами
- автоматическим фэйловером лидера занимается kafka controller

# Kafka Producers
- выбирает партицию куда записывать
- отвечает за гарантию доставки
- тюнинг производительности (отправлять batch-ами, по времени, по размеру, по кол-ву сообщений, использоватние сжатия*)
- сжатие только для больших данных, есть весь CPU
### Гарантии доставки событий продюсеру
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
# Kafka consumers
- на консюмер подписываются
- читают каждые Х секунд*
- ВАЖНО! по умолчанию консюмится пачка, но на прикладной код отдается ПО ОДНОМУ СОБЫТИЮ
- при восстановлении - начинает обратно читать с последнего `commit offset `
    - at least oncle (process - then commit)
    - mostly once (commit - then process - закомитить - после обработать)
- для консюмера можно установить
    - enable auto commit = true
    - AutoCommitIntervalMs=5000ms (you need to StoreOffsetsr
- следите чтобы оффсеты не протухали 
```
# AutoOffsetReset
Latest = 0,
Earliest = 1,
Errors = 2
```
- консюмер группы (автоматическая балансировка)
- когда брокеры раскиданы по разным дана центрам, в консюмере можно указать clientRack="DC1"
- партиции подхватываются другими живыми консюмерами
- независимая обработка разными консюмер группами
  - каждая консюмер группа коммитит свои offset-ы

# Итоги
- в rabbit mq данные хранятся в памяти (можно сделать персистентные очереди) - но кафка выиграет
- произодительностью и стримингом БОЛЬШИХ данных
- почитай про lambda architecture and K-architecture 
- данные хранятся на диске
- высокая производительность
- много консюмеров и много продюсеров
- работает буквально на уровне скорости сети

# чего нет в кафке из коробки?
- отложенных сообщений, все максимально просто
- очереди с приоритетом (всегда пишем в конец)
- TTL на сообщение (есть только в рамах сегмента)
  
# CLI commands
### Создать топик
```bash
kafka-topics --bootstrap-server localhost:9092 --create --topic hello
kafka-topics --bootstrap-server localhost:9092 --create --topic hello2 --partitions=3
```
### Cписок топиков
```
 kafka-topics --bootstrap-server localhost:9092 --list
```
### Описание топиков
```
kafka-topics --bootstrap-server localhost:9092  --describe
```
### Replication factor cannot be more than the number of brokers
```
kafka-topics --bootstrap-server localhost:9092 --create --topic hello3 --replication-factor=2
# InvalidReplicationFactorException: The target replication factor of 2 cannot be reached because only 1 broker(s) are registered.
kafka-topics --bootstrap-server localhost:9092 --create --topic hello3 --replication-factor=1 
# Created topic hello3.
```

### Удалить топик
```bash
kafka-topics --bootstrap-server localhost:9092 --delete --topic hello
```
# Kafka Console Producer CLI
- with key (distributes across all partitions)
- without key (same key always go to the same partition)

### producer 
```
 kafka-console-producer --bootstrap-server localhost:9092 --topic hello2
```
<img width="920" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/c9c03aee-f3a3-483f-aa44-5804521542d2">

### with producer property keys
```
kafka-console-producer --bootstrap-server localhost:9092 --topic hello2 --producer-property acks=all
```

### add to not declared topic 
- не объявленный топик кинет предупреждение и создастся (worked on loccalhost with one partition)

```
kafka-console-producer --bootstrap-server localhost:9092 --topic new_non_found_topic 
```

<img width="1293" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/aead86db-4049-4830-b5ab-719387ee4847">

- если эту команду запустить для распределенного кластера кинет ошибку

```
TimeOutException - ERROR when sending message to topic new_topic with key: null. ... Topic not present in metadata after 60000 ms
```

### Produce with keys
```
kafka-console-producer --bootstrap-server localhost:9092 --topic hello2 --property parse.key=true --property key.separator=:

>hello:value
>hhh
org.apache.kafka.common.KafkaException: No key separator found on line number 2: 'hhh'
	at kafka.tools.ConsoleProducer$LineMessageReader.parse(ConsoleProducer.scala:381)
	at kafka.tools.ConsoleProducer$LineMessageReader.readMessage(ConsoleProducer.scala:356)
	at kafka.tools.ConsoleProducer$.main(ConsoleProducer.scala:50)
	at kafka.tools.ConsoleProducer.main(ConsoleProducer.scala)
```

# Kafka Console Consumer CLI

- by default 16 KB of data goes to the same partition
- чтение --from-beginning начинается с самого начала когда продюсер производит хоть одно сообщение
- to consume from the beginning use `--from-beginning` (по умолчанию после поделючения начинает консюмить только новые сообщения)
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic hello2 --from-beginning 
```
<img width="928" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/199f9781-ca81-4175-85af-fce21f658c31">


### DefaultMessageFormatter for consumer CLI
- напечатать сообщения с форматтером на консоли
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic hello2 --formatter \ kafka.tools.DefaultMessageFormatter --from-beginning --property print.timestamp=true --property \ print.partition=true --property print.value=true --property print.key=true
```

<img width="1428" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/41d85f11-bdac-4cd4-a321-7dd7d1a43be4">

- produce without round robin (по умолчанию пока не заполнится 16 КБ)

<img width="1000" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/f3f043b9-19a4-4f20-94a4-1b17d82e7eb3">

- produce with **Round robin producer** - отправлять в партиции методом раунд робина (ТОЛЬКО для текущего продюсера)
  
```
kafka-console-producer --bootstrap-server localhost:9092 --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --topic hello2
```
<img width="1440" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/ddc87de1-5192-46ec-9682-1cd475fb87b7">

### Consumer groups
- лишние консюмеры в консюмер группе будут неактивными
- не имеет смысл регистрировать больше консюмеров чем кол-во партиций в топике
- если для консбмера не указать группу - он создаст temporary consumer-group
1) create topic
```
(base) tansh@MBP-tansh ~ % kafka-topics --bootstrap-server localhost:9092 --create --topic mytopic         Created topic mytopic.
```
2) append producer

<img width="1437" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/dbeade21-bb5e-45e6-971b-f0b1084773e1">

3) Добавьте группу консюмеров (может быть больше числа партиций в топике)
- опция --from-beginning имеет место только для первого консюмера, остальные повторы не начинают чтение сообщений с начала в рамках группы
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic mytopic --group group1 --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic mytopic --group group1 --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic mytopic --group group1 --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic mytopic --group group1 --from-beginning
```

4) Консюмер другой группы - начнет чтение с начала
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic mytopic --group group2 --from-beginning
```

<img width="1235" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/e1fde7c1-d12d-4bcd-b102-d69c76dc0206">

5) каждое сообщение из продюсера направится во все группы
- each consumer-group has the same data
- each consumer-group has its own independent offset
<img width="1213" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/08f19002-be4d-40d7-b5e0-18c46e6b3aae">

- нельзя уменьшать кол-во партиций, если из нее читает группа консюмеров
```
[2023-08-30 20:42:13,399] WARN [Consumer clientId=console-consumer, groupId=group1] Offset commit failed on partition mytopic-2 at offset 6: This server does not host this topic-partition. (org.apache.kafka.clients.consumer.internals.ConsumerCoordinator)
```

# Kafka consumer group CLI




### List of consumer groups

- consumer without group will accept all data from topic
- consumer without group has temporary group (`f.e. console-consumer-25698`)

```
kafka-consumer-groups --bootstrap-server localhost:9092 --list
```
<img width="841" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/76dbfb24-5ed0-4069-9fbe-5c0754cbefb7">

### Descibe one consumer group

- по `consumer ID` можно узнать, какой консюмер читает из какой партиции
```
kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --describe
```
<img width="1254" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/9b646340-375a-42f9-aa9c-0d127e805716">

## LAG - when messages produced but not consumed or committed

- `Consumer group 'group1' has no active members.`
  
<img width="1020" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/2f833314-e73f-4481-aa09-83ab151d7464">

- если подключить консюмер данной группы - ЛАГ исчезнет

<img width="1283" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/4d9b2a0e-ad5f-4fc4-9d10-d09db1923432">


# Reset consumer-group-offsets

- `--dry-run` показывает, какие партиции в топике будут сброшены
- `-execute` исполняет сброс
  
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --reset-offsets --to-earliest --topic mytopic --dry-run
```
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --reset-offsets --to-earliest --topic mytopic --execute
```
<img width="1405" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/97a70a96-e49e-43f3-8942-1d1ba9d3096b">


<img width="1060" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/c5de2569-9c8d-466d-a322-2285064ad381">

- вы не можете сбросить пока не остановите всех консюмеров из группы
```
Error: Assignments can only be reset if the group 'group1' is inactive, but the current state is Stable.
```

- `--to-latest` - default option - подключенный консюмер читает с последнего коммита (c накопленных лагов)
- `--to-earliest` - чтение с самого начала

<img width="1341" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/6358a5dc-709e-4e8c-83fc-ca589aae6542">


