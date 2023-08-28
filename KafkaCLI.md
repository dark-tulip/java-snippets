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
### Producers
- with key (distributes across all partitions)
- without key (same key always go to the same partition)


# Kafka console producing 
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

### Kafka Console Consumer CLI
- by default 16 KB of data goes to the same partition
- чтение --from-beginning начинается с самого начала когда продюсер производит хоть одно сообщение
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic hello2 --from-beginning 
```
<img width="928" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/199f9781-ca81-4175-85af-fce21f658c31">


<img width="1428" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/41d85f11-bdac-4cd4-a321-7dd7d1a43be4">

### Round robin producer 
- отправлять в партиции методом раунд робина (ТОЛЬКО для текущего продюсера)
```
kafka-console-producer --bootstrap-server localhost:9092 --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --topic hello2
```
### DefaultMessageFormatter
- напечатать сообщения с форматтером на консоли
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic hello2 --formatter kafka.tools.DefaultMessageFormatter --from-beginning --property print.timestamp=true --property print.partition=true --property print.value=true --property print.key=true
```

<img width="1000" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/f3f043b9-19a4-4f20-94a4-1b17d82e7eb3">


- produce without round robin

<img width="1440" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/ddc87de1-5192-46ec-9682-1cd475fb87b7">


- produce with round robin

![Uploading image.png…]()

