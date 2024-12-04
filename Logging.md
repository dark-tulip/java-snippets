- логирование часто связывают с аспектами

### Формат и вывод логируемых сообещений, Appender, Layout, Category

- `root` содержит все логгеры в системе
- any logger generates message
- `Category` contains many loggers
- `Appender` adds loggers to file or any stdout
- `Layout` adds timestamps or message formats
- Логгеры и категории связываются по именам (Имя логгера начинается с имени категории)
- Категория и аппендеры связаны по appender reference
- фильтрация на уровне аппендера и категории берется по наиболее приоритетному

### Настройка конфигурационного файла логирования
1) Set VM options
```
-Djava.util.logging.config.file=logging.properties
```
2) Define configuration file
```
# Глобальный уровень логирования, принимает все
.level=ALL
# Обработчик логов - вывод в консоль
.handlers=java.util.logging.ConsoleHandler
# Обработчик консольных логов также выводит все уровни приоритета
java.util.logging.ConsoleHandler.level=ALL
```

3) Output
```
# Без
> Task :Main.main()
Hello world!
Mar 30, 2024 10:09:20 PM kz.tansh.Main main
INFO: info logger

# C настроенным файлом
> Task :Main.main()
Mar 30, 2024 10:02:46 PM kz.tansh.Main main
INFO: info logger
Mar 30, 2024 10:02:46 PM kz.tansh.Main main
FINEST: FINEST logger
Hello world!
```

### When spring boot does not show logs

create logback-spring.xml file on the project resources

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <logger name="your.name" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
</configuration>
```

###run local memcached and redis 

```
docker run -d --name memcached-local -p 11211:11211 memcached
docker run -d --name redis-local -p 6379:6379 -e REDIS_PASSWORD=jedis redis --requirepass jedis
```

```mvn
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
```

### java.util.logging.Handler

обработчик определяет куда будет отправлено сообщение
```
java.util.logging.ConsoleHandler
java.util.logging.FileHandler
java.util.logging.SocketHandler
```

### java.util.logging.Formatter
```
java.util.logging.SimpleFormatter
java.util.logging.XMLFormatter
```
