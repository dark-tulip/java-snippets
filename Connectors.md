## Ограничения Kafka
#### Гарантия доставки сообщений:
- "At-least-once" или "at-most-once" гарантии по умолчанию. 
- Достижение "exactly-once" требует дополнительных настроек, таких как idempotent producer и transactional consumer.
- При сбоях могут возникать дубликаты сообщений.
#### Латентность:
- Возможна задержка при передаче данных в высоконагруженных системах из-за сетевой перегрузки или высокой нагрузки на брокеры.
#### Партиционирование:
- Если данные распределяются по партициям, порядок сообщений сохраняется только внутри одной партиции.
- Возможна несогласованность данных между партициями при сложных трансформациях.
#### Размер сообщений:
- Ограничение размера сообщения (по умолчанию 1 МБ). Для передачи больших данных требуется разделение или сжатие.

## 2. Ограничения Debezium
#### Зависимость от источника данных:
- Debezium сильно зависит от источника данных (например, Postgres, MySQL) и их механизмов журналирования изменений (CDC — Change Data Capture).
- Если журнал транзакций источника данных очищается слишком быстро, может быть потеряна история изменений.
## Производительность:
- При больших объемах данных (например, большой объем записей в CDC) возможны задержки в обработке.
## Сложности с обработкой DDL (изменений схемы):
- Не все изменения схемы в источнике корректно обрабатываются. Например, добавление новых столбцов может потребовать ручного вмешательства.
## Поддержка ограниченного набора баз данных:
Только популярные реляционные базы данных (Postgres, MySQL, MongoDB, Cassandra и т.д.) имеют полную поддержку.

## 3. Ограничения Sink/Source коннекторов
#### Ограниченная совместимость и конфигурация:
- Коннекторы могут не поддерживать все возможности приемника (например, специфичные индексы или ограничения MongoDB).
- В случае сложных схем данных (например, вложенных документов) могут быть сложности с трансформацией.
#### Гибкость трансформаций:
- Kafka Connect поддерживает трансформации через SMT (Single Message Transformation), но они ограничены по функционалу. Для сложных ETL требуется писать собственные процессоры.

#### Производительность:
- MongoDB Sink Connector может быть медленным при больших объемах данных из-за необходимости обработки JSON-документов.
- ClickHouse-коннектор (если используется) требует оптимизации под батчевые записи.

#### Гарантии транзакционности:
- Большинство коннекторов не поддерживают ACID-операции при записи данных.
- MongoDB Sink Connector работает с "at-least-once" гарантией доставки, но не обеспечивает "exactly-once".
## 4. Ограничения Kafka MongoDB Source/Sink Connector

#### Производительность при больших объемах данных:
- MongoDB Sink Connector может быть узким местом из-за ограничений MongoDB (например, write locks).
- MongoDB Source Connector работает медленно при большом объеме данных или множестве шардов.

#### Сложности с вложенными структурами:
- MongoDB работает с документами JSON, а Kafka — с сериализованными объектами (Avro, JSON). Коннектору может быть трудно **обработать вложенные структуры**.

#### Поддержка изменений схемы:
- **MongoDB не имеет явной схемы, что делает сложным обнаружение изменений структуры данных**

#### Обработка удаления записей:
- MongoDB Sink Connector может **некорректно обрабатывать события удаления**, если они не настроены явно.

#### Сложности настройки и поддержки:
- Конфигурация коннекторов может быть сложной, особенно при использовании нестандартных источников или приемников.
- Отсутствие автоматической обработки ошибок требует настройки ретраев и мониторинга.
  
#### Мониторинг и отладка:
- **Трудно отследить ошибки в процессе передачи данных**, особенно в распределенных системах.

## Возможные пути решения
#### Для производительности:
- Настроить партиционирование и replication factor в Kafka.
- Оптимизировать настройки batch size и linger.ms для Sink Connector.
#### Для консистентности:
- Включить поддержку "exactly-once" в Kafka (idempotent producer и transactional consumer).
- Использовать Debezium для захвата полного состояния данных (snapshot + log).
#### Для схемы данных:
- Использовать Avro или Protobuf для строгой сериализации сообщений.
- Настроить трансформации (SMT) для приведения данных к стандартной схеме.
#### Для мониторинга:
- Интегрировать Kafka Connect с инструментами мониторинга (Prometheus, Grafana).
- Настроить алерты на ошибки синхронизации.