## REST это про:
- стиль построения веб служб
- единица информации URL - это достук к ресурсу, а HTTP метод - глагол
- HTTP Status - для указания ошибок
- Каноничный
- Стандарт общения
- Stateless - не хранит состояние клиента

## Storages
- Session storage - deletes after close
- Local Storage - can be deleted only programmaly
- both session and local storage are accessible from JS

## Domain and origin difference
- origin это тройка протокол домен и порт 
- Cookie задается на весь домен
- Local storage позволяет храть данные для данного origin
- CSRF - когда злоумышленник умеет отправлять запросы от другого пользователя
- CORS - cross origin resource sharing

### Что еще есть помимо реста?
- `REST` means - repsesentation state transfer - формат запрос - ответ
- `WEBSocket` - открываем постоянное соединение
- `Polling` - отправили запрос, сервер сам решит когда вернуть ответ
- `GRAPHQL` - как рест, но сами выбираем поля которые хотим получить в ответе
- `P2P` - децентрализованное общение без сервера между клиентами напрямую

### HTTP headers
- Заголовок `HTTP X-Forwarded-For` - используется для определения изначального IP адреса клинета, когда запрос пересылается между балансировщиками или HTTP-proxy


### Rest in Java
- Методы контроллера в `Java` вызывает `DispatcherServlet`
- с контроллерами работает `DispatcherServlet`
- утилизация = это время нагрузки / общее время работы
- Сервлет позволяет принимать и обрабатывать сетевые запросы без создания сетевой инфраструктуры
- Протокол TCP/IP означает что на транспортном уровне у нас протокол TCP, а на сетевом IP 

### Идемпотентность 
- при повторных вызовах - не меняет исходное состояние данных
- возвращает один и тот же результат
- HTTP headers - `Idempotency-Key: *UUID*`
- например, избежать двойного списания с карты*

  
**Public APIs are forever, you have one chance to get it right**
- minimize accessibility
- allow or restrict inheritance
- версия это важный источник информации о совместимости (Major.Minor.Patch-Label)

### Cтабильный переход это
- SOLID
- много юнит и UI тестов
- DI на интерфейсах
- dog-fooding своего API
- ПИШИТЕ о НЕСТАБИЛЬНОСТИ
- политика версионирования

<br>

- use UUID
- берегитесь инкрементальных ИД
- NEVER send token througn URL
- use content-type HTTP headers
- описывает контракт между потребителем и поставщиком сервиса
- удобен для CRUD операций

## Вебсокеты 
- полнодуплексное соединение - когда и клиент и сервер могут отправлять сообщения одновременно
- это полнодуплексное (постоянное) соединение (двухстороннее)
- обмен данными livetime
- энергоемкий, особенно для мобильных устройств
- можно отправлять сообщения одному, нескольким или всем клиентам (например, в группе)
- переключение с `http/https` на `ws/wss`: `Connection: Upgrade, Upgrade: WebSocket, Sec-WebSocket-Key`.

## Вебхуки
- без установки соединения
- идет подписка по URL адресу на вебхук
- при возникновении события отправляется запрос клиенту
- риск потерять данные
- работает с публичными URL (security issues)


## Http headers
- `must-revalidate and max-age: 0 is same as no-cache`


## Circuit breaker
- ресурсоемкий по отношению к основному приложению
- **OPEN**
- **HALF-OPEN**
- **CLOSED**
- памяти затратно (if only you use redis or memcache)
