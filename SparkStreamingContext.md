Обработка событий
- event-driven
- batch
- microbatch

```bash
spark2-submit hdfs-wordcount.py 2>/dev/null
```

**statefull** - хранит состояние (хранит в RDD)
**stateless** - не хранящее состояние (прыгающее окно и сбрасывающее state)
- чекпойнт обязателен, без него через `updateStateByKey` не запустится `StreamingSparkContext()`. Именно через `updateStateByKey` делается `statefull` обработка событий в потоке

```python
import os
import time
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext

from hdfs import Config
import subprocess

client = Config().get_client()
nn_address = subprocess.check_output('hdfs getconf -confKey dfs.namenode.http-address', shell=True).strip().decode("utf-8")
sc = SparkContext(master='yarn')
DATA_PATH = "/data/course4/wiki/en_articles_batches"
batches = [sc.textFile(os.path.join(*[nn_address, DATA_PATH, path])) for path in client.list(DATA_PATH)]
BATCH_TIMEOUT = 2 # Timeout between batch generation
ssc = StreamingContext(sc, BATCH_TIMEOUT)

dstream = ssc.queueStream(rdds=batches)

def print_rdd(rdd):
    for row in rdd.take(10):
        print('{}\t{}'.format(*row))

def aggregator(values, old):
    return (old or 0) + sum(values)

result = dstream \
    .flatMap(lambda line: line.split()) \
    .map(lambda word: (word, 1)) \
    .updateStateByKey(aggregator) \
    .reduceByKey(lambda x, y: x + y) \
    .foreachRDD(lambda rdd: print_rdd(rdd.sortBy(lambda x: -x[1])))

ssc.checkpoint('./checkpoint{}'.format(time.strftime("%Y_%m_%d_%H_%M_%s", time.gmtime())))  # checkpoint for storing current state  

ssc.start()
ssc.awaitTermination()
```
![image](https://github.com/user-attachments/assets/2775ed86-04df-4002-ad01-4b137d744a67)
