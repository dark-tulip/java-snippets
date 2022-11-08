## 1. Внутренние области памяти

### Stack
- используется только одним исполняемым потоком
- Сохраняется ссылка на объект, а в heap сам объект
- Стековая память недоступна для других потоков
- StackOverFlowException
- only compiler instructions manages stack memory
- Память освобождается в конце выполнения метода
- хранение примитивных типов

#### Стековая память
- Permanent generation - память в JVM для хранения метаинформации 
- Code cache - память при использовании JIT компиляции (кэшируется скомпилированный - платформенно зависимый код)

---

### Heap
- основной сегмент памяти где хранятся все объекты
- Общий участок памяти для всего приложения
- OutOfMemoryException
- хранение ссылочных типов

#### Области памяти в heap
- old generation, 
    - Tenured (долгоживущие объекты - после переполнения выполняется тотальный GC) - статичные объекты, константы, огромные objects
- new generation
    - eden space (все новые объекты создаются тут, когда переполняется запускается GC)
    - survior space (после первой сборки мусора)

## 2. Garbage collectors

Триггерит GC:
- переполнение объектов в куче
- ОС посылает сигнал
GC выолняет только две задачи:
- reference counting (каждый объект имеет счетчик ссылок на себя - когда ноль мусор)
- tracing (узлы до которых нельзя достучаться (non-reachable objects) из GCroot считаются мусором)

    
#### copy collection - эффективен когда много мусора и мало полезных обхектов
- Помечает неиспользуемые
- Копируются объекты которые нужны в survior
- удаляет все из eden

#### mark-sweep-compact collection 
- Пометка неиспользуемых
- Sweep - удаление пустых слот ( removes marked objects)
- Compact - заполнение пустых слот (смещение к ним)

### generational gc 
по умолчанию JVM HotSpot имеет 4 сборщика мусора
Решает проблемы:
- dangling pointers (указателей на удаленный объект)
- auto clearing of unused memory space
- memory leak (утечка памяти - програмный код не может достучаться до объекта)
- автоочищение медленней чем правильней вручную
- GC is memoty management tool
- tracks every available object


## 3. GC types

#### 1. Serial GC
- один из первых
- GC работает одним потоком
- замораживает все потоки приложения
   - minor GC - new generation
   - mark-sweep-compact - total - old generation - с уплотнением
   
#### 2. Parallel GC
- Использует несколько потоков для управления heap space
- Также замораживает другие потоки приложения
- default for Java 8


#### 3. Concurrent-Mark-Sweep
- multiple GC threads 
- can share processor resources with GC (running application)
- shorter pauses
- deprecated in Java 9, dropped from java 14

#### 4. G1GC - garbage first garbage collector
- default from Java 9
- разбиение кучи на набор области одинакового размера
- хорошая пропускная способность
- from new -> old geneeration
- parallel marking of unlinked objects 
- сжатие живых объектов путем паралельного копирования
- JVM sets the region size (depends from heap size)
- eden, survior and olg generation are logical sets of these regions (они не пересекаются)
- firsl, compact collection sets 
- second, Remembered sets to track references into regions (to scan only region without whole heap)

#### 5. Zero GC
- для больших куч с меньшей задержкой
- работает паралельно с приложением 
- Java 11 -> 15

#### 6. JVM withot GC
- from java 11
- stops JVM when куча переполнена
- Epsilon - no op GC


--- 
#### про эффективную работу с памятью:
- from java 8 - duplicated strings storing
- файл лучше читать построчно а не целиком (no large objects)
- string builder
--- 

- Try with resources
#### Cashing and redis
