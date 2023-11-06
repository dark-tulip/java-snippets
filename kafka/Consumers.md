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
- Если консюмеру не указать топики - кинет исключение (consumer is not subscribed to any topics or assigned any partitions)
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

## Partition Reassignment between consumers
- when new consumer leaves or joins the group
- when new partition adds into a topic

## Consumer re-balancing strategies
**(1) Eager re-balance (STW)**
- в течение ребалансировки отключается вся группа консюмеров
- заново перераспределяются (рандомно, консюмер который читал из одной партиции может читать из другой)

**(2) Cooperative re-balance (Incremental)**
- reassigns small subset of partitions 
- other consumers will not be interrupted (and continue working on the same partition)
- minimizes the number of partitions for reassignment, works smart

- есть распределение консюмеров по партициям:
- учитывающие ие подписки на топики 
- и НЕ учитывающие подписки на топики

### Use `partition.assignment.strategy` for consumer in Properties
- некоторые старые версии не учитывают подписанные топики

**1. RangeAssignor**
- число партиций делится на кол-во консюмеров (может неравномерно)
- разделяет диапазоны партиций по группам и присваивает каждому консюмеру
Например, если у вас есть 10 партиций и 2 консюмера, один потребитель получит партиции с 0 по 4, а другой - с 5 по 9.

**2. Round Robin Assignor**
- используется для минимизации нерабочих консюмеров (когда число консюмеров больше кол-ва партиций)
- не сбалансирован когда консюмеры неравномерно подписаны на топики* - whatch 2nd example in the source code
```bash
For example, we have three consumers C0, C1, C2, 
and three topics t0, t1, t2, with 1, 2, and 3 partitions, respectively. 
Therefore, the partitions are t0p0, t1p0, t1p1, t2p0, t2p1, t2p2. 
C0 is subscribed to t0; 
C1 is subscribed to t0, t1; and 
C2 is subscribed to t0, t1, t2.

That assignment will be:
C0: [t0p0]
C1: [t1p0]
C2: [t1p1, t2p0, t2p1, t2p2]
```

**3. Sticky assignor**
- eager rebalance protocol
- revokes all partitions during joining group

**4. Cooperative Sticky Assignors**
- consumers can keep on consuming from the topic

### Static group membership
- Когда консюмер покидает группу, его партиции revokes and reassigns
- когда упавший консюмер возвращается в группу, it will have new `member-id` and new partition assigned
- `group.instance.id` делает консюмер статичным членом (uniq id of consumer in consumer group)
- пока не пройдет `session.timeout.ms` времени, партиция будет ожидадть оживления консюмера
- если консюмер не восстановится в течение `session.timeout.ms`, it will trigger partition rebalance
- `group.instance.id = null` which is default null
- WARNING! consumer will be static when `group.instance.id` has value, and until `session.timeout.ms expires = 45000`, group will not be rebalanced!
- Why we use? - To avoid group re-balances with larger timeout (or revoking all partitions)
- `heartbeat.interval.ms` = 3000 is used by default with dynamic members
- задав эту настройку мы только через 45 сек узнаем что консюмер упал `properties.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "444");`

по умолчанию используется 
```log
partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor, class org.apache.kafka.clients.consumer.CooperativeStickyAssignor]
```
### how to upgrade configs?
- use rolling bounce (последовательный перезапуск консюмеров)
- чтобы поочередно перезапустить каждый консюмер и изменить стратегию assign-мента для каждого консюмера в группе
- тем самым предотвратив stop the world from consumer group
- избежать простоя системы

### How Cooperative Sticky Assignor works?
```log
properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
```
first instance
```log
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Updating assignment with
    Assigned partitions:                       [topic1-0, topic1-1, topic1-2]
    Current owned partitions:                  []
    Added partitions (assigned - owned):       [topic1-0, topic1-1, topic1-2]
    Revoked partitions (owned - assigned):     []

################# after adding new consumer:

	Assigned partitions:                       [topic1-0, topic1-1]
	Current owned partitions:                  [topic1-0, topic1-1, topic1-2]
	Added partitions (assigned - owned):       []
	Revoked partitions (owned - assigned):     [topic1-2]
	
	Assigned partitions:                       [topic1-0, topic1-1]
	Current owned partitions:                  [topic1-0, topic1-1]
	Added partitions (assigned - owned):       []
	Revoked partitions (owned - assigned):     []
```

added the second consumer instance 

```log
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Updating assignment with
	Assigned partitions:                       []
	Current owned partitions:                  []
	Added partitions (assigned - owned):       []
	Revoked partitions (owned - assigned):     []
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Notifying assignor about the new Assignment(partitions=[])
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Adding newly assigned partitions: 
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Request joining group due to: group is already rebalancing
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] (Re-)joining group
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Successfully joined group with generation Generation{generationId=14, memberId='consumer-group1-1-6eeb01f3-c08a-45d0-b2f1-8653c921bb75', protocol='cooperative-sticky'}
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Successfully synced group in generation Generation{generationId=14, memberId='consumer-group1-1-6eeb01f3-c08a-45d0-b2f1-8653c921bb75', protocol='cooperative-sticky'}
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Updating assignment with
	Assigned partitions:                       [topic1-2]
	Current owned partitions:                  []
	Added partitions (assigned - owned):       [topic1-2]
	Revoked partitions (owned - assigned):     []

[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Notifying assignor about the new Assignment(partitions=[topic1-2])
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-group1-1, groupId=group1] Adding newly assigned partitions: topic1-2
```

## Consumer offsets
- default at least once 
- when you call `poll()` and `auto.commit.interval.ms` has ellapsed
- `auto.commit.interval.ms = 5000` and `enable.auto.commit = true` is default
- оно срабатывает когда с момента последнего поллинга данных прошло больше 5ти сек, проходит автокоммит ДО след poll()
- WARNING! you've already proceeded all messages before commit (and poll()) again!
- когда вы отключили автокоммит, и обрабатываете данные, возможно в другом потоке, `commitSync()`, `commitAsync()` with the correct offsets manually


<a href='https://www.youtube.com/watch?v=NXU_F_7STSM&list=WL&index=42'>Григорий Кошелев — Когда всё пошло по Кафке 3: Apache Kafka и Consumer</a>

#### LogEndOffset - когда добавилась запись из продюсера (но не факт, что фолловеры подгрузили)
#### HighWatermark - оффсет, среплицированный фоловерами (now consumers can consume the data)

### Consumer под капотом
- KafkaConsumer - consumes records from kafka cluster (взаимодействуем по коду)
- ConsumerMetadata - metadata (кто лидер, кто не лидер) - updating timer
- SubscriptionState - следит за подписками (tracks topic, partition and offsets) may be `USER_ASSGNED` or `AUTO`
- ConsumerNetworkClient (обертка для сетевого клиента консюмера)
- NetworkClient (async request/response network i/o, TCP connections, implements KafkaClient interface)
- ConsumerCoordinator - consumer group management strategy and polling the data
- Fetcher - чтение с нужной позиции в нужном колве

### assign VS subscribe 

#### assign - ручками
- `assign(Collection<TopicPartitons> partitions)`
- adding partitions (NOT topics!!!) by hand
- добавление / изменение кол-ва нужно обновлять ручками
- чтение конкретных событий
#### subscribe 
- `subscribe(Collection<String> topics)`
- auto assigning of new partitions
- `subscribe(Pattern pattern)` auto subscription to topics by regex 

```log
assign (consumer) 
-> assignFromUser (subscription) 
-> requestUpdateForNewTopics (metadata) 
-> call seek (consumer)
-> poll (consumer)
-> validate and reset offsets (consumer from fetcher)
-> fetcher goes to subscription -> send to client by queue
-> client wait for networkClient
-> networkClient goes to kafka cluster
```
- после - консюмер вообще не ждет получения метаданных от кафки и сразу же отправляет следующий запрос

### Seek strategies (will change coordinates)
- from beginning
- from end
- from given position

#### default polling size:
- `max.poll.records=500` 
# <b>ПОВТОРИ ПРИМЕРЫ Java Kafka Examples</b>
### https://www.conduktor.io/kafka/advanced-kafka-consumer-with-java/

## Delivery semantics
**at most once**
- `accept batch -> commit -> process`
- НЕ прочитает первую пачку обратно, начнет читать с коммита (точнее с пачки2)
- `| (batch1) OK OK OK X X | (batch2) OK OK OK OK OK`

**at least once (recommend)**
- `accept batch -> process -> commit` 
- прочитает первую пачку обратно, потому что первая пачка еще не закоммитилась
- `| (batch1) OK OK OK X X | (batch1) OK OK OK OK ОК`


## Стратегия коммита сообщений
### 1. По умолчанию, автокоммиты
- по умолчанию пачка коммитится когда вызывается poll method
- `auto.commit.interval.ms=5000` and `enable.auto.commit=true` achieved
- before calling poll again make sure your messages are already processed
- consumer полит, полит, полит сообщения пока не истечет таймер коммита и не отправится auto commit
- will automatically committed at regular interval
```java
while(true) {
  var batch = consumer.poll(Duration.ofMillis(1000));
  processSync(batch);  // process the data
}
```
### 2. Manual committing
- `enable.auto.commit=false` и синхронная обработка пакетов
- в этом случае вы контролируете стратегию коммита сообщений (например собрать буфер и махом отправить в БД)
- можно задать свои условия в isReady до коммита пачки принятых сообщений
```Java
while(true) {
  var batch += consumer.poll(Duration.ofMillis(1000));
  if (isReady(batch)) {
    processSync(batch);
    consumer.commitAsync();
  }
}
```

**!!! ВНИМАНИЕ
`ConsumerConfig.ENABLE_AUTO_COMMIT_СONFIG=false` property каждый раз при запуске одной и той же консюмер группы будет читать сообщения c его самого последнего коммита (ну или с начала если это новая группа), 
а ЛАГ не будет исчезать из партиций КАФКИ - это погубит систему**

## Вставка пачками BulkRequest in openSearch
- помимо чтения данных батчами из кафки, в OpenSearch можно посылать эту пачку массовым запросом (`BulkRequest`)
- составляется документ (record for openSearch) в виде `IndexRequest`, копится в BulkRequest
- вызывается `openSearchClient.bulk(bulkRequest)`

## Сброс оффсета
- (это настройка на брокере) ПО УМОЛЧАНИЮ ОФФЕТЫ КОНСЮМЕРА МОГУТ СБРОСИТЬСЯ
- до кафки 2.0 старые данные стирались каждый день
- с кафки 2.0 каждые 7 дней

#### offset.retention.minutes 
данный конфиг обнуляется когда
- консюмер группа потеряла всех своих членов и истекло время offset.retention.minutes
- когда консюмер группа больше не коммитит сообщения в данную партицию а консюмер группа больше не подписана на этот топик
- когда для вручную назначенных консюмеров истекло время момента последнего коммита 
- когда вы мануально удаляете консюмер группу или топик - оффсеты будут удалены сразу же и безвозвратно
#### Как правильно обновлять?
- отключить консюмер группу
- сменить оффсет данной группе
- запустить обратно
#### По умолчанию стратегия чтения (стартовая точка чтения консюмер группой `auto.offset.reset`)
- latest (default)
- earliest
- none (если нет никакого предыдущего оффсета - кинет исключение)

## Преимущество оффсетов
- каждая консюмер группа читает независимо, имеет свой оффсет
- наличие оффсета позволяет перечитать данные с начала сегмента (offset=0)
- каждый консюмер в консюмер группе читает ТОЛЬКО ПО ОДНОЙ партиции (от подписанного топика или топиков)
- если при чтении данных произошла алгоритмическая ошибка - данные можно перечитать с начала оффсета
- !!! при запуске программы обратите внимание на `auto.offset.reset` параметр!!! - если читать с самого начала - это ресурсоемко, и может убить приложение
