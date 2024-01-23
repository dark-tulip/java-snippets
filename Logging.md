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
