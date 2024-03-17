### Внутренности JOIN
- **CROSS JOIN** - декартово произвидение всех записей (соотв условию `table1 INNER JOIN table2 ON true`). Кол-во записей равно N * M
- **INNER JOIN** все записи соответсвующие условию соединения (ON or USING clause) - по умолчанию подразумевается INNER JOIN для JOIN
- **LEFT JOIN** (сначала выполняется INNER JOIN, затем добавляются строки из левой таблицы не удовлетворяющие условию соединения, а значения с правой стороны заполняются нуллами)
- **RIGHT JOIN** аналогичен left join.
- для left, right, full join используется OUTER
- для просто join по умолчанию INNER
- Пара строк из t1 и t2 соответствуют если условие ON для них истинно
- **условие помещенное в ON исполняется ДО операции соединения, а where после**

### IS TRUE, equals TRUE, IS NULL, IS UNKNOWN DIFFERENCE
- `show transform_null_equals;` -- по умолчанию off
- `set transform_null_equals = on;` заменяет все = null на is null
- `show all;`

```sql
create table test(
    boo boolean
);

insert into test
values (null),
       (true),
       (false),
       (1::bool),
       (0::bool);

select * from test where boo is true;        -- true, true
select * from test where boo = true;         -- true, true
select * from test where boo = false;        -- false, false
select * from test where boo is null;        -- null
select * from test where boo is unknown;     -- null (тип проверки для is unknown обязательно должен быть booleanб)
select * from test where boo is not unknown; -- true, false, true, false
select * from test where boo = null;         -- empty result
select * from test where boo is false;       -- false, false
select * from test where boo is not true;    -- null, false, false 
select * from test where boo is not false;   -- null, true, true
select * from test where boo != false;       -- true, true
select * from test where boo != true;        -- false, false

select null = null;   -- null
select null = true;   -- null
select null = false;  -- null
select null is null;  -- true
select null is true;  -- false
select null is false; -- false
select null is unknown; -- true
select unknown is null; -- error
```
```sql
+---------+-------+-------+---------+
|   IS    | TRUE  | FALSE | UNKNOWN |
+---------+-------+-------+---------+
| TRUE    | TRUE  | FALSE | FALSE   |
| FALSE   | FALSE | TRUE  | FALSE   |
| UNKNOWN | FALSE | FALSE | TRUE    |
+---------+-------+-------+---------+

+---------+---------+---------+---------+
|    =    |  TRUE   |  FALSE  | UNKNOWN |
+---------+---------+---------+---------+
| TRUE    | TRUE    | FALSE   | UNKNOWN |
| FALSE   | FALSE   | TRUE    | UNKNOWN |
| UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN |
+---------+---------+---------+---------+
```
**! Единственная разница между операторами `is` and `=` это то как они работают c `NULL` значениями**

### Курсоры
- явные (имеют имя и работают в оперативной памяти)
- неявные (каждый раз идет обращение к памяти с диска)

# JSON in POSTGRES
- JSON удобен для хранения разнородных метаданных одной и той же структуры
- в постгресе есть JSONB который появился из HSTORE
- можно хранить весь JSON в одном столбце
- потрудитесь ID вынести из JSON-a для быстрого поиска (держите вне JSON-а)
- JSONB может замениться на TOAST pointer (скрытая таблица, куда данные перемещаются)
- запись помещается в TOAST (где разделяется на чанки в сжатом виде ссылаясь через tuple pointer) когда кортеж превышает 1/4 часть страницы (8кб)
- скорость доступа падает с увеличением JSON-a
- запись сжимается, если сжатая запись не умещается в 2 КБ - нарезается на тосты
- чтобы ускорить декомпрессию JSON-а было принято разжимать chuck by chuck, также и наоборот, сначала нарезаем а потом сжимаем
- UPDATE в JSON работает очень тупо, при изменении одного байта перезаписывается весь JSON
- когда обновляется id внутри большого JSON-a запись попадает в WAL где имеет весь свой размер - решением было разделить обновление на чанки, чтобы в WAL шла новая версия этого чанка
- **JSON выигрывает когда у вас много JOIN-ов между разными сущностями** - позволяет быстее достать готовый объект
- МИНУС: обновление большого JSON, медленный поиск внутри текста (массива) json*

### Как преобразовать колонку типа текст json
1) update column values (from plain text to json text format)
2) alter column using cast

```sql
update test_table set name=json_build_object('default', name, 'ru', name);
alter table test_table alter column name type jsonb using name::jsonb;
```

![image](https://github.com/dark-tulip/course-java/assets/89765480/0a34cfdf-b043-4ad5-97c3-61610e32b6d0)


# TOAST
- если вы хотите хранить больше чем 2 кб, вы должны ззнать что такое TOAST



# Logged and unlogger tables
- поднятая в контейнере БД может дать ускорение буквально до двух раз для нелогируемых таблиц
- for unlogger tables WAL is disabled
- можно включить и выключить на лету, только из нелогиуремой в логируемую займет достаточно времени - почему? - потому что unlogged table будет записывать все свои данные в WAL чтобы мочь реплицироваться
- не логируемые таблицы НЕ реплицируются, существуют только в рамках одного инстанса 

# Optimisation
- read only transactions которые не записываются в WAL
- параллелизм зло если load average (top) выше числа процессоров
- индексы которые не используются месяц
- некоторые FK можно дропнуть на проде - то что уже регулируется языком бэкенда (FK кушают перформанс на вставках)
  
# Internal work
- если два события взаимосвязаны - это взаимосвязь она наблюдаемость любого процесса в этой системе
- на все шарды отправляется запрос потом эти данные по кусочкам собираются (монго дб)
- синхронизация времени проходит по времени кластерам (clusterTime) - время назначается конфиг сервером
- wall clock time - время потраченное на исполнение задачи включая сететвые задержки
- CPU time - время потраченное вычислениями

- SQL query -> RAM(worker thread -> shared memory) (and WAL) -> disk
- dirty page - изза любой измененной записи целая страница считается грязной
- если упали - страницы восстанавливаются по WAL - так идет восстановление до самой последней транзакции

- БД используют страницы фиксированного рахмера (для алгоритмов сериализации) - хранить данные страницами удобно - страничная модель
- транзакции это способ ускорения, НЕ ЗАМЕДЛЕНИЯ - параллельная обработка данных и эффективно расходовать ресурсы
- при записи вся страница помечается как ГРЯЗНАЯ
- чтобы можно было восстановить страницы записываем в WAL
- помимо WAL есть еще чекпойнты (синхронизация страниц на диск)
# DO NOT TUNE THE QUERY / KNOW YOUR DATA
## Пессимистические алгоритмы шедулинга транзакций - решают проблему конфиликтов сериализации
### MVCC - multiversion concurrency control
- MVCC это сделать 2PL быстрее
### 2 phase locking
- для не распретеленных транзакций
- НЕ ПУТАЙТЕ 2PL c MVCC
### 2 phase commit
- для распределенных транзакций

Отвыкай от книг и переходи к документации
- да окей, читай для теории - но лучше дока
- ЧИТАЙ `postgresql.conf` - почитай про каждую инструкцию
### мои записки по базам данных были утеряны :(
- GIN
- GIST
- HASHINDEX
- BITMAP INDEX
- BTREE INDEX
- BTREE+ INDEX

### WAL журнал

### AUTOVACUUM

### WORK MEM
