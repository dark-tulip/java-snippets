### Тестирование внешних систем 
- C моками можно имитировать corner cases
- Мок библиотека для юнит тестов
- WireMock может имитировать ответ от сервера на запрос
- верифицировать произведенные вызовы
- Spy object - шпионить перехватывая запросы к настоящему сервису

### Моки
это как юнит тестирование с какими то предположениями про БД
- Быстрые и надежные
- Проще запустить
- Быстрее написать
- Не работают как настоящая система
- Ограничены при интеграционных тестах

### Тестконтейнеры
- Когда моков недостаточно
- Страшные 
- Проблемы со скоросью запуска
- Настоящий черный ящик
- Интерграционные тесты с настоящими сервисами асинхронные с неопределенныеми трудностями

TopologyTestDriver
Работает синхронно как один event loop в браузере
Kafka
Работает асинхронно во многих тредах и контейнерах

Гарантировать асинхронный тест нам ничего не может
h2 - in memory bd
- статртует с пустой БД
- после теста состояние забывается
- мигрируется моментально


Integration - сервис
Integrated - сервисы

Integration testing transformation
- Mocking
- LocalDbs
- Vagrant (оркестрирует VMs)
- Docker (вместо виртуальных машин запускают контейнеры)
- Fig (aka Docker Compose)
- Docker API

Test containers
- Обертка поверх dcoker-java
- Прибить контейнеры
- Программирование зависимостей (докер контейнеров в java коде)

ResponseEntity.accepted() - сначала принять запрос а уж потом обработать
ResponseEntity.ok() 

Port numbers are generated randomly