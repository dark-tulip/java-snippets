# Интеграция с ClickHouse 

- Нет транзакций и блокировок
- СУБД не гаратирует что данные сразу появятся (асинхронная передача)
- Обновить или удалить конкретную строку - нецелесобразно
- RAM usage, Оперативка не менее 4 ГБ

1. Время вставки одной строки и 10к одинаково (лучше большими батчами)
2. Не читайте по одной строке - используйте статистику
3. выбирайте только нужные столбцы
4. Аггрегируйте данные на стороне ClickHouse (меньше данных передается по сети) и на бэке меньше вычислений

_Подходит для аналитики и стат иследований_

## ClickHouse Kafka Connect Sink
The Kafka Connector Sink is distributed under the Apache 2.0 License

## Как настроить CDC между топиком кафки и кликхаусом

### -- Шаг 1: Создать ClickHouse-таблицу для Kafka
```sql
-- kafka_books_raw не хранит данные, а только читает новые сообщения из Kafka в реальном времени.
-- Данная таблица работает как очередь, которая позволяет ClickHouse читать данные из Kafka без внешних консьюмеров (Python, Java).
-- ClickHouse по умолчанию ожидает "плоский" JSON, поэтому нам нужно извлекать только payload
-- Мы используем JSONAsString, чтобы сначала загрузить полные сообщения, а потом парсить их:
CREATE TABLE kafka_books_raw (
    raw_message String
) ENGINE = Kafka
SETTINGS kafka_broker_list = 'kafka-1:19092',
         kafka_topic_list = 'source_db.public.books',
         kafka_group_name = 'clickhouse-consumer',
         kafka_format = 'JSONAsString',
         kafka_max_block_size = 65536;
```

### Шаг 2: Создать таблицу хранения
```
-- Сообщения читаются только один раз → Если ClickHouse уже прочитал сообщение, то при следующем SELECT в kafka_books_raw оно исчезнет.
-- Она работает как "стрим" → В отличие от MergeTree, Kafka-таблица просто отображает поток данных, а не хранит их.
CREATE TABLE books_storage (
    id Int64,
    title String,
    author String,
    published_year Int32
) ENGINE = MergeTree()
ORDER BY id;
```

### Шаг 3: Создадим материализованное представление, которое будет разбирать JSON и сохранять данные:

```sql
-- ClickHouse не копирует данные вручную, а использует Kafka как источник событий.
-- автоматически передаёт их в MergeTree через MATERIALIZED VIEW.
-- Когда Kafka отправляет новое сообщение: ClickHouse-консьюмер читает сообщение в kafka_books_raw

CREATE MATERIALIZED VIEW kafka_to_books
TO books_storage
AS
SELECT
    JSONExtractInt(raw_message, 'payload', 'id') AS id,
    JSONExtractString(raw_message, 'payload', 'title') AS title,
    JSONExtractString(raw_message, 'payload', 'author') AS author,
    JSONExtractInt(raw_message, 'payload', 'published_year') AS published_year
FROM kafka_books_raw;

SELECT * FROM kafka_books_raw;
```


