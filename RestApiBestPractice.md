- утилизация = это время нагрузки / общее время работы


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
