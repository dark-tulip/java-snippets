### Проект работы с рефлексией на Java

1. new Proxy Instance (interface)
2. ByteBuddy (create instance from class, not interface in Runtime)


### 1. Задание. Результат ответа (run psvm main)

```
== Первый вызов:
Вызов оригинального метода
Начальное значение
== Второй вызов:
 == get result from cache
Начальное значение
== Обновили состояние закэшированного значения:
== Третий вызов:
Вызов оригинального метода
Установили Новое значение
== Четвертый вызов:
 == get result from cache
Установили Новое значение
```


### 2. Во втором задании
- Здесь уже Proxy из JDK не подойдёт,
  тк он работает только с интерфейсами.
  Придётся использовать библиотеку,
  способную создавать прокси по классам — например, CGLIB или ByteBuddy.

1. **CGLIB** (Code Generation Library) — библиотека для создания runtime-прокси на уровне байткода,
   позволяет наследовать классы и переопределять их методы (не требует интерфейсов, как java.lang.reflect.Proxy)
2. **ByteBuddy** - библиотека Java для динамического создания и модификации классов во время выполнения,
   которая перехватывать вызовы методов, даже если классы не реализуют интерфейсы
   (это то что нам нужно при выполнении задания, чтобы перехватывать исполняемые методы)


### Для запуска тестов используйте
```bash
mvn clean test
```


```logs
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running kz.lab.task2.CachingProxyV2Test
 == called method from Child class == 
 == called method from Child class == 
 == called: methodV2Cached
 == called: methodV2Cached
 == get from cache methodCached :: == called: methodV2Cached
 == get from cache methodCached :: == called: methodV2Cached
 == get from cache methodCached ::[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.219 s -- in kz.lab.task2.CachingProxyV2Test
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.505 s
[INFO] Finished at: 2025-04-13T15:59:10+03:00
```
