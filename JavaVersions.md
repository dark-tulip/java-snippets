Сравнение версий java

- https://javaalmanac.io/

### Java 9 
- immutable collections List.of()
- Completable future асинхронный код
- OrElse optional
- Работать со стэк трейсом без создария объекта exception
- Objects require not null
- Http2 с поддержкрй асинхронных запросов
- Разбиение функциональности на отдельные модули, всего jdk, а некотррые модули были вынесеня из jdk
- Запуск единичного java файла,
- Var в лямбда функциях 
- Method referebce в стримах
- Опционал из емпти
- Методы isblank(), Strip()

### Java 17
- шаг в сторону оптимизации gc on realtime, особенно когда вы платите за вычислительные ресурсы
- неиспользуемая память возвращается ОС
- Виртуальные потоки в java, не аллоцируются ОС, Расходуют меньше памяти, на каждый поток раньше выделялось по 1024 кб, теперь это гораздо легковесней
- Виртуальные потоки не привязаны к физическим потокам ОС
- Виртуальные потоки подходят для задач которые быстро завершаются
- Создаются из Executor service virtual treads per task. Имеют облегченный стек
