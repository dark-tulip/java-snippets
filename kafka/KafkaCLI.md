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


