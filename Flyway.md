Flyway - фреймворка для непрерывного изменения схемы базы данных 

Flyway обновляет версии баз данных с помощью миграций. Миграции можно писать на SQL (с синтаксисом, специфичным для конкретной СУБД) или на Java.

Миграции
Версионные - запускаются только один раз
Повторяющиеся - только при изменнеии контрольной суммы


configuration pom.xml
```
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>${flyway.version}</version>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/library</url>
        <user>postgres</user>
        <password>postgres</password>
    </configuration>
</plugin>
```
