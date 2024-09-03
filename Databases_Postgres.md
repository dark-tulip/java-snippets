- основное преимущество носкл - очень быстро менять схемы данных и json формат

# Sharding
- Идеально когда целый селект идет в один шард а не в несколько
- Range based sharding
- Hashed key based sharding
- Directory based sharding


### Особенности работы постгресса
- любой update это delete + insert
- любой update на pk это перевычислиение идекса относительно его позиции
- любой delete - это помеченные к удалению автовакуумом записи
- страница НЕ имеет версию, версию имеет тапл, расположенный на странице
- Грязные - это еще НЕ синхронизированные с диском данные
- snapshot isolaton is MVCC implementation
- сначала данные сохраняются на диске, только потом изолируются
- есть пять уровней изоляции - пятый это снапшоты свойственные постгресу
- RETURNING позволяет возвращать модифицируемые строки при INSERT, UPDATE, DELETE


## Мониторинг индексов, не используемые индексы

```sql
select schemaname || '.' || relname                   as tablename,
       indexrelname                                   as index,
       pg_size_pretty(pg_relation_size(i.indexrelid)) as index_size,
       idx_scan                                       as index_scans,
from pg_stat_user_indexes ui
         join pg_index i on ui.indexrelid = i.indexrelid
where not indisunique
  and idx_scan < 50
  and pg_relation_size(relid) > 5 * 8192
order by pg_relation_size(i.indexrelid) / nullif(idx_scan, 0) desc nulls first,
         pg_relation_size(i.indexrelid) desc;
```

## Внутренние настройки постгреса
- чем выше **maintenance_work_mem тем быстрее создастся индекс**
```
select current_setting('maintenance_work_mem');
```

### Жизненный цикл страницы в постгресе
`shared_buffers <-> OS cash <-> disk`

## Чекпойнты
- один из основных источников проблем производительности на запись
- чистые страницы поднимаются в shared_buffers, если хоть один кортеж изменился - страница помечается грязной
- commit не возвращает управления пока страница не записана в WAL
- время от времени происходит чекпойт, грязные страницы из shared_buffers пишутся на диск
- при больших значениях shared_bufferrs возникают проблемы
- если shared_buffers слишком большие - диски их не тянут
- **вся запись это fsync**

### Index hits and index vs seq scan stats
- индекс говорит где в таблице мы можем найти эту запись
- индекс всегда отсортирован, в рамках каждой страницы все данные отсортированы по индексу
- concurrent indexы помогают избежать блокировки таблицы
   
```sql
-- Одна строка для каждого индекса в текущей базе данных, показывающая статистику ввода-вывода по этому конкретному индексу. Подробности см pg_statio_all_indexes.
-- Одна строка для каждого индекса в текущей базе данных, показывающая статистику обращений к этому конкретному индексу. Подробности см pg_stat_all_indexes.
--
-- idx_blks_read bigint
-- Количество дисковых блоков, прочитанных из всех индексов в этой таблице.
--
-- idx_blks_hit bigint
-- Количество попаданий в буфер во всех индексах этой таблицы
--
-- idx_scan bigint
-- Количество сканирований индекса, инициированных по этому индексу
--
-- idx_tup_read bigint
-- Количество записей индекса, возвращенных при сканировании этого индекса
--
-- idx_tup_fetch bigint
-- Количество строк активной таблицы, полученных простым сканированием индекса с использованием этого индекса.

select * from pg_stat_user_indexes;
select * from pg_statio_user_indexes;

-- Определение наиболее нагруженных таблиц
select
   *
from pg_stat_user_tables
order by n_tup_upd + n_tup_ins + n_tup_del DESC;

-- Отношение сканирований по индексам к последовательным сканированиям
select
   relname,
   seq_scan,
   idx_scan
from pg_stat_user_tables
order by seq_scan DESC;
```

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
- MVCC это как таймстамп когда прочтени и когда изменено
- предлагает хорошую конкурентность
- в условиях значительной read/write активности
- читатели не блокируют писателей и наоборот

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

### WAL (Write Ahead Logging) Buffers
- определяет как готовые записи попадут на диск (временное хранение изменений до сохранения на диске)
- желательно по 16 MB
- значительно уменьшает кол-во записей на диск
- изменения попадают на диск только после того, как эти изменения будут залогированы
- когда в БД попадает множество конкуррентных транзакций, одна операция fsync операция для WAL может быть гораздо эффективней для фиксации множества транзакий
- это логический журнал, где каждая запись содержит информацию о транзакции (номер, длину записи, ссылка на предыдущую запись, отвечающий менеджер ресурсов, контрольная сумма) 


### WAL журнал
- Репликация на основе WAL появилась с 9 го постгреса
- чтобы коммит вернул управление должна произойти запись в WAL (if syncronous_commin = on)
- **в WAL пишется информация необходимая для ВОССТАНОВЛЕНИЯ**
- запись происходит из WAL_buffers, которые после каждого коммита происходят целиком
- один WAL file имеет размер 16 MB
- 
- pg_xlog на диске
- определяет как готовые записи попадут на диск
- до появления WAL использовали целые снапшоты
- WAL помошает восстановиться до момента последней успешной транзакции перед сбоем
  
### AUTOVACUUM
- работает в виде демона автовакуума
- это подчистка помеченных к удалению данных, которые выпали из области видимости транзакции
### WORK MEM
- сколько памяти будет потреблять этот зарпос? - `3 * work_mem`
```
select * from (select * form table1) t1 join (select * from table2) t2 on t1.id = t2.id
```


### Поля массивы в постгресе
```sql
create table holiday (
       holiday varchar(50),    -- строковое значение
       sandwitch text[],       -- массив
       side text[][],          -- многомерный массив
       desert text array,      -- массив      
       beverage text array[4], -- массив из 4 элементов
)
```
