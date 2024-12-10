## JSON -> PARQUET

#### Нормализация вложенных данных:
- Вложенные структуры (например, "address")
- преобразуются в плоские колонки (address_city, address_zipcode).

#### Сжатие данных:
- Parquet сжимает данные (по умолчанию Snappy),
- поэтому файл data.parquet будет значительно меньше, чем data.json.

#### Формат хранения:
- Parquet-файл нельзя просто открыть текстовым редактором,
- но он идеально читается аналитическими системами (например, Spark, Pandas)

```python
import pandas as pd
from pandas import json_normalize

# ============= 1. save json to parquet format
data = [
    {
        "id": 1,
        "name": "Alice",
        "age": 30,
        "email": "alice@example.com",
        "address": {
            "city": "New York",
            "zipcode": "10001"
        }
    },
    {
        "id": 2,
        "name": "Bob",
        "age": 25,
        "email": "bob@example.com",
        "address": {
            "city": "San Francisco",
            "zipcode": "94105"
        }
    }
]

df = json_normalize(data, sep='_')  # json flattening

parquet_file = 'data.parquet'
df.to_parquet(parquet_file, engine='pyarrow', index=False)
print(f" ==== saved to parquet file {parquet_file}")


# ============= 2. read from parquet
df_parquet = pd.read_parquet(parquet_file)
df_parquet
```

<img width="496" alt="image" src="https://github.com/user-attachments/assets/7137d1c6-3f1c-4428-8b19-ce237ed421f2">


## Проблемы переноса данных из разных схем
- kafka jdbc sink connector не знает куда переносить данные, нужно задавать вручную, через конфиги коннектора (мэппинг схем и таблиц в конфигурации Sink Connector)
- альтернатива - использовать обработчики (Kafka Connect SMT) или промежуточные преобразователи (Kafka Streams)

новое:
- у source коннектора есть пропертя **"query"** можно задать его чтобы объединить результат с нескольких таблиц
- для `sink` коннектора важно чтобы ему передали точное название топика **`direct topic name`**
- для `sink` коннектора есть настройка **"table.name.format": "target_schema.${topic}"**, которая указывает в какую схему и таблицу перенести данные

если коннектора недостаточно, всегда можно использовать Kafka Streams:

```java
StreamsBuilder builder = new StreamsBuilder();
KStream<String, GenericRecord> sourceStream = builder.stream("source-schema-table1");

sourceStream.map((key, value) -> {
    // Преобразуем данные, например, добавляем целевую схему
    value.put("schema", "target_schema");
    return KeyValue.pair(key, value);
}).to("target-schema-table1");

KafkaStreams streams = new KafkaStreams(builder.build(), properties);
streams.start();
```
