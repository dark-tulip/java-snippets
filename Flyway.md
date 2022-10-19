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
        <locations>filesystem:src/main/resources/db/migration/</locations>
        <schemas>public</schemas>
    </configuration>
</plugin>
```

Naming
```
- prefix (V)
- version (major_, minor)
- name (double __name )
```

By default Flyway will look for migrations on the classpath under db/migration, which on a Maven project means src/main/resources/db/migration
```Java
    static final String url = "jdbc:postgresql://localhost:5432/library";
    static final String user = "postgres";
    static final String password = "postgres";
    
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            Flyway.configure().dataSource(url, user, password).load().migrate();
            // do nothing
        };
    }
```
