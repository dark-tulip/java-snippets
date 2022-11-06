## 1. Какие внутренние области памяти есть

### Heap
- основной сегмент памяти где хранятся все объекты
- Общий участок памяти для всего приложения
- OutOfMemoryException

#### Области памяти в heap
- old generation, 
    - Tenured (долгоживущие объекты - после переполнения выполняется тотальный GC)
- new generation
    - eden space (все новые объекты создаются тут, когда переполняется запускается GC)
    - survior space (после первой сборки мусора)

### Stack
- используется только одним исполняемым потоком
- Сохраняется ссылка на объект, а в heap сам объект
- Стековая память недоступна для других потоков
- StackOverFlowException
- only compiler instructions manages stack memory

#### Стековая память
- Permanent generation - память в JVM для хранения метаинформации 
- Code cache - память при использовании JIT компиляции (кэшируется скомпилированный - платформенно зависимый код)
---

## 2. очистка памяти

GC выолняет только две задачи
    - reference counting (каждый объект имеет счетчик ссылок на себя - когда ноль мусор)
    - tracing (узлы до которых нельзя достучаться (non-reachable objects) из GCroot считаются мусором)

Spoiler. Утечка памяти - когда два объекта ссылаются друг на друга и не имеют внешних ссылок

    
#### copy collection - эффективен когда много мусора и мало полезных обхектов
- Помечает неиспользуемые
- Копируются объекты которые нужны в survior
- удаляет все из eden
#### mark-sweep-compact collection 
- Пометка неиспользуемых
- Sweep - удаление пустых слот
- Compact - заполнение пустых слот (смещение к ним)

### generational gc 

--- 

- Try with resources
- GC
- Сколько heap and stacks имеет GC
- Serial GC
- Parralel GC
- G1GC
- without GC

2. Какие garbage collectorы есть
3. Java varsion и GC
4. Внутренняя реализация


#### gc for heap or stack\?

#### Cashing and redis
