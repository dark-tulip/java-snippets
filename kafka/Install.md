## Install kafka on Mac M1

```bash
## Install via brew
brew install kafka

## Kafka configs
cd /opt/homebrew/etc/kafka

## Zookeeper configs
cd /opt/homebrew/etc/kafka

## Location for kafka data
cd /opt/homebrew/var/log/kafka
```

## Kafka with Zookeeper server start
start zookeeper first
```bash
zookeeper-server-start /opt/homebrew/etc/zookeeper/zoo.cfg
```
then start kafka
```bash
kafka-server-start /opt/homebrew/etc/kafka/server.properties
```
## Start Kafka in KRaft mode (without zookeeper)
- generate id for cluster

```bash
kafka-storage random-uuid
```

- location of kraft server properties
```bash
/opt/homebrew/etc/kafka/kraft/server.properties
```

- format log dir and replace in the config/kraft/server.properties file, default /tmp/kraft/combined-logs
```bash
kafka-storage format -t 1l18DkhPTemKEvKEcwYEbA -c /opt/homebrew/etc/kafka/kraft/server.properties
# Formatting /opt/homebrew/var/lib/kraft-combined-logs with metadata.version 3.4-IV0.
```

- если прочитать содержимое:
```
...
# A comma separated list of directories under which to store log files
# log.dirs=/opt/homebrew/var/lib/kraft-combined-logs
...
```

start kafka with kraft properties
```bash
kafka-server-start /opt/homebrew/etc/kafka/kraft/server.properties 
```
