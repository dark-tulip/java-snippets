1) Установка и настройка кластера
Дата центры иногда называются **racks**
- установка и настройка кластера не такая легкая задача как кажутся
- необходима синхронизация между всеми брокерами
- зукипер и кафка брокер должны быть на разных серверах (проблема поиска ошибок)
- также необходимо внедрить мониторинг

Другие компоненты для установки
- kafka schema registry - make sure to run two for high availability
- Kafka connect Clusters 
- UI administration tools (like Conduktor)

2) После установки кластера нужно настроить мониторинг
- для корректного мониторинга состояния брокеров, да, они тоже могут падать
- кафка предоставляет метрики с помощью JMX (java management extensions)
- to make sure that the system is behaving correctly under the load

Где обычно размещают метрики кафки?
- ELK
- Prometheus
- Datalog
- NewRelic
- ConfluentControlCentre

Важные метрики
- under replicated partitions - реплики отстающие от лидера
- request handlers, IO, network visualization
- request timing, как долго возвращается ответ на запросы

# Проблемы безопасности
- каждый может соединиться к кафка кластеру. Проблема аутентификации
- каждый может публиковать, читать данные с кластера. Проблема авторизации
- данные передаются по сети в открытом виде. Проблема unencrypted data
- enc in java jdk 11 got a whole lot better
- ssl based encryption можно настроить на кафке (in flight encryption via SSL)

## SSL & SASL Based authentication 
- только аутентифицированные клиенты могут сконнектиться к кластеру
1) аутентификация с помощью SSL сертификатов
2) SASL / PLAINTEXT -
    * где клиенты аутентифицируются с помощью логина и пароля,
    * нужно включить SSL шифрование на стороне брокера
    * password changes require brokers reboot (good only for dev)
3) SASL / SCRAM
    * username / password + salt
    * также нужно включить SSL шифрование на стороне брокера
    * no need to restart when you add or delete user (auth data is stored and configured by Zookeeper)
4) SASL / GSSAPI
    * Kerberos: как microsoft AD (очень сложен в настройке)
    * самый безопасный
    * для enterprise
5) SASL / OAUTHBEARER
    * использует OAUTH2 токены

## Authentication
- настраивается с помощью ACL на топики
- узнав identity пользователя брокер может распознать его авторити к топикам

# Replication 
- реплицируется не ОФФСЕТ, а ДАННЫЕ в кластере 
- репликация не сохраняет смещение, она лишь сохраняет данные
- **WARNING! данные под одним смещением могут различаться от данных с таким же смещением в другом кластере**
- репликация работает просто, это продюсеры и консюмеры кафки

## 1) Active - Active cluster replication
- оба кластера активны
- conflicts in data reads (асинхронные операции обновления)
- возможность разделить пользователей на ближайший кластер (+ к перформансу)
- redundancy (резервирование) and resilience (устойчивость) - если один дата центр упадет, пользователей можно перенаправить на другой

## 2) Active - Passive cluster replication
- хорош для миграций
- целый кластер бездействует
- прост в настройке
- невозможен failover без потери данных или их дублирования

# Network (Сеть между кафка клиентом и брокером)
Advertise listener - the most important setting in Kafka
Every kafka broker has
- PUBLIC_IP
- PRIVATE_IP
- ADV_HOST
- **каждый клиент начинает обращение к брокеру кафки с публичного IP адреса**
- чтобы установить соединение **брокер требует обращения по advertised host name**


- если клиент кафки не может достучаться до ADV_HOST, он не сможет соединиться с брокером
- по умолчанию adv_ip = private_ip
- if adv_ip = localhost - и клиент запущен на той машине - это будет работать; но если локально поднято два брокера - пойдут конфиликты
- можно установить adv_ip равный public_ip, но это грозит потерей adv_ip если у самой хост машины сменится публичный IP адрес (когда изменится public_ip, но adv_ip останется со старым значением) - no network route to the kafka server
#### Advertised ADDRESS - объявленный адрес для обмена данными
### `advertised.listeners` - what to set
- if your client on PRIVATE network
  - internal private IP
  - internal private DNS hostname 
- if your client on PUBLIC network
  - external public IP
  - external public hostname, pointing to public IP


