# Advanced message queuing protocol
- general purpose message queuing protocol
- TCP connections (TLS + username + passwd)
- publish -> exchanger -> route -> queue -> consumers
- routes messages to queues (топик обменника перенаправляет сообщения в очереди)
- очереди отправляют консюмерам
- both sync and async
- 2 GB max , 123 MB recommend
  
# Message Queuing Telemetry Transport
- specially designed IoT
- publisher / subscriber architecture
- использует MQTT topics
- TCP connections (TLS + username + passwd)
- работа при постоянной потере связи
- легковесный
- asynchronous msg mode by event-based
- can queue msg
- 256 MB max


- `maxConcurrentConsumers` - кол-во консюмеров которые могут одновременно подключаться для обработки сообщений
- `prefetchCount` - предзагрузка столького кол-ва сообщений (в виде пачки чтобы не нагружать сеть) который брокер отправляет в виде порции

| Kafka | RabbitMQ |
| --- | ----------- |
| Network speed, up to 1M msg per second | 4-10K |
| Stored messages (two weeks default) | Removes after acceptance |
| Stupid broker smart consumer (Polling model) | Smart broker stupid consumer - (Pushing model) |
| Publisher subscriber delivery model | направленный, веерный, топик, по заголовкам |
| Размер сообщения опт ДО 1 МБ | ДО 2Гб, рекоменд ДО 128 МБ - НО НЕ ФАКТ что не упадет |
| Операционный | Транзакционный |
| Гарантируется порядок в рамках партиции | Без гарантии атомарности |
| Порядок на базе партиций | Нет упорядочивания |
| Нет приоритета сообщений | Назначаемы |
| Целые потоки данных, последовательность событий | Сложная маршрутизация |
| Потоки порядка 100к сообщ и выше | Поддержка STOMP, MQTT, AMQP |
| Витринные данные - собирать собственную БД от топика - сливать в целевые БД, а не лезть в персистентное хранилище | временная очередьь, для тонких очередей |

