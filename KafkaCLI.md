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
```

```

## Connect to conductor cluster
1. 

## create topic

```bash

```
