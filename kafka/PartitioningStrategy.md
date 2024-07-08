https://redpanda.com/guides/kafka-tutorial/kafka-partition-strategy

**Default partitioner**
- распределение по хэш ключу
- если его нет идет по раунд робину

**RoundRobin partitioner**
- распределение по карусели

**UniformSticky partitioner**
- при инициализации продюсер прикрепляется к партиции
- сообщения копятся пока не накопится batch.size или не пройдет время linger.ms

**Сustom partitioner**
- доступна собственная имплементация
