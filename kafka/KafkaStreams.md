# What is Kafka Streams?
- для обработки потока данных внутри кафка кластера
- выявление аномалий (по безопасности)
- потоковая обработка и преобразование
- мониторинг и уведомления
- это стандартное Java приложение
- нет необходимости создавать отдельный кластер
- отказоустойчива
- гибка в настройке и масштабировании
- идемпотентность при передаче данных (exactly once)
- нет батчинга. Одна запись на единицу времени
- stream process means in addition some logic between data translation
- functional Java API
- filtering, grouping events, aggregating, joining streams
- scalable, fault-tolerance state management
- consumers tend to grow in their complexity
- key aggregation, additional logic, delivery semantic settings,


## Как настроить?
- включите контейнеры проекта `kafka/conduktor-platform/docker-compose.yml`
- запустите продюсер на топик `wikimedia.recentchange`
- запустите стримы

Топики появятся в консоли платформы
- базовый топик, от которого читает кафка стрим это `wikimedia.recentchange` (сюда продюсер собирает все данные)

![img_4.png](img/img_4.png)

Основные настроенные процессоры находятся в папке `processors` пишут в свои топики
- `wikimedia.stats.bots`

![img_2.png](img/img_2.png)

- `wikimedia.stats.timeseries`

![img_1.png](img/img_1.png)

- `wikimedia.stats.website`

![img_3.png](img/img_3.png)

- все остальное называется внутренними топиками - созданными приложением kafka-streams для функционирования
и сохраняют свои метаданные в материализованных внутренних хранилищах:
- `bot-count-store`
- `event.count.store`
- `website-count-store`


