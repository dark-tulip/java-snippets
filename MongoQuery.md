Performance is: 
- about testing,
- about tuning about making sure that all the components working together,
- about identifying what is bottleneck

# Premature optimisation is the root of all EVIL!!!
*so, programmers waiste enormous amounts of time... worrying about.. the speed of noncritical parts*  
- each index reduces write performance
- field order matters in the compound inxdex

## 4 principles of chosing SHARDING KEY
- **HIGH CARDINALITY** Высокая кардинальность (большое кол-во уникальных значений, например гендер будет обладать низкой кардинальностью)
- **EVEN DISTRIBUTION** равномерное распределение, каждое значение не должно повторяться (age = uneven distribution, because you may have more younger users that the older ones)
- **FREQUENTLY USED IN QUERIES** - так, чтобы по запросу было понятно в какой шард идти, key should be part of the query, but id is a bad example cause of it is scatter/gather query (требует перебора по всем шардам для находжения документа)
- **NON MONOTONIC** - не монотонно возврастающий ключ

## Решение
- хэширование ключа подходит в большистве случаев - делает любое поле рандомно распределяемым
- подумайте распределение шардов по зонам
- составной ключ с низкой кардинальностью (с рандомным значением) в симбиозе ascending id

## Перебалансировка данных в шардах
- балансировка построена на шардах
- с 5.0 версии монги поддерживается live rebalancing between shards
- данные в кластере разбиваются по чанкам,
- за равномерное распределение чанков отвечают шарды
- шард отвечает за хранение и обслуживание чанков 
- чанки это наборы документов
- когда **balancer window** выявляет перегруженные шарды, он может включить процесс миграции чанков в другой шард (наименее загруженный), этот процесс называется **CHUNK MIGRATION** - миграция фрагментов
- запустить балансировку можно вручную `sh.stopBalancer()` и `sh.startBalancer()`
- sharding key это поле или набор полей которые выбираются для разделения данных по чанкам
- данные с одинаковым ключом шардирования попадают в один и тот же чанк
- balancer window enabled by default in mongodb 4.2
  
**HOT SPOTS** - горячая точка, когда один шард получает слишком много запросов изза неравномерного распределения

# Props of using mongoDB (large amount of data)
- mongodb has flexible schema, but it doesn't mean you can ignore design
- нет схемы и динамическая структура данных (НО! лучше когда в коде есть структура)
- flexible масштабирование (добавление шардов)
- преимущество!! для SQL пишут целые книги как добавить столбец в таблицу
- нет транзакций (до 4 версии)
- нет join-ов (lookup with unwind to immitate left outer join)
- lookup:
-   коллекция для объединения
-   поле в коллекции
-   поле во внутренней коллекции
-   название нового поля
- unwind: из поля в виде массива, разложит каждый документ по отдельности
- максимальный размер документа в монге 16 MB


# Read Preference 
- по умолчанию и запись и чтение идет из primary 
- `secondaryPreferred`  - если все реплики недоступны - читает из мастера
- `secondary`  - если все реплики недоступны - кидает ошибку

# Read concern and write concern
- начиная с монги 4.4 можно установить глобальные конфиги на согласованность записи и чтения
- операции где не указан read concern используют global read concern
- репликация данных является асинхронной
- majority and linearizable вернут данные, которые не будут rolled back
- арбитр не хранит данные, он является кворум контроллером, который выбирает primary БД
- in mongo does not exist MultiMaster

# Oplog
- операционный журнал хранит историю изменений для реплик
```
use local
db.oplog.rs.find().sort({$natural:-1}).limit(1)
```

# db.stats()

- покажет статистику базы данных, fsUsedSize может различатся изза сегментирования и разности файловых хранилищ в ОС
`db.stats()`


## MongoDB

- MongoDB имеет свойство заполнять всю предоставленную ей (т.е. не используемую) оперативную память - это нормальное поведение
- особенно при индексации данных, старается закэшировать все данные в RAM
- можно разделить RAM usage for Index and Data
- по умолчанию ограничений нет, можно подключаться столько, сколько позволяют ресурсы ОС
- connection pool общих как для пользовательских, так и служебных соединений
- длинное название поля увеличивает размер документа
- порядок полей имеет значение
- самый высокий cardinality у поля `_id`
- шарды работают только с чанками, которые распределяет балансировшик
- аггрегированные запросы работают в виде пайплайна действий над данными
- in mongodb sync is PULL NOT PUSH

## Особенности 
- бесконечное хранилище
- спец object id для любого документа, всегда уникальный, инкрементальный
- BSON - binary json
- хранимые процедуры на готовом javaScript коде
- запись идет в память, по дефолту каждые 100мс дампим на диск
- большая фрагментация данных при удалении записи - много пустых удаленных блоков
- добавление идет в коннец
- шардирование без даун тайма
- есть compact команда для фрагментации
- есть repair базы которые полностью транкейтят, но памяти нужно 2х - тут полная блокировка на уровне всей БД
- по дефолту нет консистентности
- getLastError() есть команда

## Индексы
- В дерево
- композитные индексы
- по массиву или поддокументу
- выбор лучших индексов - работает оптимизатор
- `explain()` - execution plan

## Repplication
- операции записываются в oplog - operation log
- изменение в документе описывается сеттерами
- репликация идет по поллинг модели - реплика полит у мастера - мастер просто записывает в оплог
- voting процесс для выбора мастера
- голосование инициирует машина которая перестала видеть мастера
- запись только в мастер, все реплики приходят к состоянию нового мастера. Если некоторые реплики были впереди мастера, их данные попадут в рековери лог для ручной обработки - все КОНФЛИКТЫ РЕЗОЛВЯТСЯ ВРУЧНУЮ
- КВОРУМ это большинство

## Sharding
- автоматическая балансировка после добавления новой инстанции
- шардирование по зоне
- config server работающий с двухфазным коммитом - без него не будет балансировки
- выбирается коллекция для шардинга
- выбирается поле по которому будут строиться чанки
  
## подручные проблемы
- индексы
- железо 
- кэширование

## Медденная запись или уровни Durability guarantees
в монге на каждый запрос гарантию можно установить отдельно
`{w:1,j:true}` - не ждать подтверждения записи большинством*
`{w:1,i:false}` - отключить подтверждение в журнал
`{w:0,j:false}` - отключить любое подтверждение (latency, but not througput)


```javascript
db.Person
  .aggregate([
    {
      $match: {
        $and: [{status: 'ACTIVE'}, {actual: true}],
        $nor: [{isRoot: true}],
        $nor: [{isSystem: true}]
      }
    },
    {$addFields: {type: "PERSON"}},
    {
      $project:
        {_id: 1, name: {$concat: ["$name", "$surname"]}, type: 1, email: 1, companyId: 1}
    },

    {
      $unionWith: {
        coll: "Department",
        pipeline: [
          {$match: {"actual": true}},
          {$addFields: {type: "DEPARTMENT"}},
          {$project: {_id: 1, name: 1, type: 1, companyId: 1}}
        ]
      },
    },

    {
      $unionWith: {
        coll: "PersonGroup", pipeline: [
          {$match: {"active": true}},
          {$addFields: {type: "GROUP"}},
          {$project: {_id: 1, name: 1, type: 1, companyId: 1}}
        ]
      }
    },
    {
      $match: {companyId: ObjectId('6f27ebce5e49a79d69e522a8')}
    },
    {
      $addFields: {
        sortByIds: {
          $cond: {
            if: {
              $in: ["$_id", [
                ObjectId('fb1a992e15b53adf9df0dc61'),
                ObjectId('abcfaaca0ab24f66475de98b'),
                ObjectId('a74227440d8a3a94f3227d7e'),
                ObjectId('f06da2d108585002105f341c'),
              ]]
            },
            then: 0,
            else: 9999999
          }
        }
      }
    },
    {$sort: {sortByIds: 1, type: 1, name: 1}}
  ]);
```


### Получить внутренние записи коллекции
```
db.getSiblingDB("db_name").getCollection("coll_name").aggregate([
  {
    $project: {
      events: {
        $objectToArray: "$events"  // превратить в key/value документ, чтобы ссылаться ниже через точку, где K это ИД события, а через V можно достать его внутренности
      }
    }
  },
  {
    $unwind: "$events"  // дернуть документ за веточку выше
  },
  {
    $project: {
      _id: 1,
      eventType: "$events.v.type",
      fieldValues: { $objectToArray: "$events.v.fieldValues" },
      participantIds: { $objectToArray: "$events.v.participantIds" },
      happened: "$events.v.happened"
    }
  },
  {
    $unwind: {
      path: "$fieldValues",
      preserveNullAndEmptyArrays: true
    }
  },
  {
    $unwind: {
      path: "$participantIds",
      preserveNullAndEmptyArrays: true  // разрешить пустые значения в ячейках, нужно для полной проекции
    }
  },
  {
    $project: {
      _id: 1,
      eventType: 1,
      fieldId: "$fieldValues.k",
      newValue: "$fieldValues.v.newStoredValue",
      oldValue: "$fieldValues.v.oldStoredValue",
      participantId: "$participantIds.k",
      happenedAt: "$happened.at",
      happenedBy: "$happened.by"
    }
  },
])
```

