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
**Eager rebalance**
- в течение ребалансировки отключается вся группа консюмеров
- заново перераспределяются (рандомно, консюмер который читал из одной партиции может читать из другой)

**Cooperative rebalance (Incremental)**
- reassign small subset
- other consumers will not be interrupted (and continue working on the same partition)
- minimizes the number of partitions for reassignment, works smart

### Use `partition.assignment.strategy`
- RandomAssignor 
