MapReduce
- разбивает задачу на маленькие части, обрабатывает их параллельно (map), а затем агрегирует результаты (reduce).

Сегодня MapReduce постепенно заменяется более эффективными технологиями, такими как: 
- `Apache Spark` → быстрее, так как хранит данные в памяти.
- `Apache Flink` → потоковая обработка данных.
- `Dask` → распределенные вычисления на Python.

### Как работает MapReduce?
**1: Разбиение данных (Input Splits)**
- Перед запуском `MapReduce Hadoop` разбивает входные данные на части (`splits`)
- Если у нас 100 ГБ данных и кластер из 10 машин, Hadoop автоматически разобьет их, чтобы каждая машина обрабатывала только свою часть.

**2: Фаза Map (Карта)**
- `Mapper` получает кусочек данных
- Обрабатывает их и выводит пары ключ-значение `(key → value)`.

**3: Shuffle & Sort (Перемешивание и сортировка)**
- Hadoop перетасовывает данные так, чтобы все значения для одного ключа оказались вместе (sorting)

**4: Фаза Reduce (Сведение)**
- Reducer получает сгруппированные данные и агрегирует их

**5: Запись результатов в HDFS**
- Финальные данные сохраняются в HDFS в несколько файлов `part-00000, part-00001`
- _Количество файлов зависит от числа Reducer'ов_

---- 


- Combiner

- in mapper combiner используют когда мы уверены что у ключа мало значений (года, сезоны, ограниченное множество)

### Partitioner
- можно переопределить только через Java класс

### Pair vs Stripes

- пары используют для потоковой аналитики
- Пары. Чтение и запись последовательно
- Страйпы. Запись блоками.
- Stripes используется когда у нас в маппере мало ключей
- Stripes - аналог колоночного хранения данных
- Пары медленнее чем страйпы (за счет большого кол-ва не уникальных значений)

## Задача подсчет кол-ва слов в тексте

**run.sh**
```bash
#!/bin/bash

OUT_DIR="hobmapreducetask1_result"
NUM_REDUCERS=4

# check then delete
hadoop fs -test -d $OUT_DIR && hadoop fs -rm -r -skipTrash $OUT_DIR > /dev/null

# run Hadoop Streaming via yarn
yarn jar /opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-streaming.jar \
    -D mapreduce.job.reduces=${NUM_REDUCERS} \
    -files mapper.py,reducer.py \
    -mapper "python3 mapper.py" \
    -reducer "python3 reducer.py" \
    -input /data/wiki/en_articles \
    -output $OUT_DIR > /dev/null

# print
hdfs dfs -cat ${OUT_DIR}/part-0000* | head -10
```


**mapper.py**
```python3
#!/usr/bin/env python3
import sys
import re
import string

# regex for the task
WORD_REGEX = re.compile(r'\b[A-Z][a-z]{5,8}\b')

lowercase_words = set()

for line in sys.stdin:
    parts = line.strip().split("\t", 1)
    if len(parts) != 2:
        continue

    _, text = parts
    text = re.sub(r"[{}]".format(string.punctuation), "", text)  # remove пунктуацию
    words = text.split()

    for word in words:
        if word.islower():
            lowercase_words.add(word)  # memory слова в нижнем регистре
        elif WORD_REGEX.fullmatch(word):
            print("{}\t1".format(word.lower()))  # print via `\t`

```

**reducer.py**
```python3
#!/usr/bin/env python3
import sys
from collections import defaultdict

word_count = defaultdict(int)

for line in sys.stdin:
    word, count = line.strip().split("\t")
    word_count[word] += int(count)

# sort by second column desc, then first column: сначала по убыванию количества, затем по алфавиту
sorted_words = sorted(word_count.items(), key=lambda x: (-x[1], x[0]))

for word, count in sorted_words:
    print("{}\t{}".format(word, count))
```
