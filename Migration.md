### how to run in maven
накатить последний change-set
```bash
mvn liquibase:update -Dliquibase.changesToApply=1
```

### Свойства миграций
- обладать атомарностью. Группа миграций накатывается целиком либо никак
- обратимость - есть способ возврата к предыдущейму состоянию
- упорядоченность - есть порядок выполнения миграций

### Теоретический материал по миграции

1. Преимущественно использовать групповые операции
2. По умолчанию каждая строка кэшируется и пакетом отправляется при вызове `executeBatch()`
3. (Streaming batch inserts) Потоковая вставка, каждый раз вставляет в БД при вызове `addBatch()`

### Batch Inserts Using JDBC Prepared Statements

https://www.vertica.com/docs/9.2.x/HTML/Content/Authoring/ConnectingToVertica/ClientJDBC/BatchInsertsUsingJDBCPreparedStatements.htm

- `addBatch()` - вставляет строку в пакет
- `executeBatch()` - выполнить пакетную вставку
 
```Java
try (Connection connection = DriverManager.getConnection(connectionUrl)
    // 1. Create a prepared statement
    PreparesStatement ps = connection.PreparedStatement("INSERT INTO clients(client_id, name, surname) VALUES (?, ?, ?)")

    // 2. Add rows to a batch in a loop. Each iteration adds a new row.
    for (int i = 0; i < firstNames.length; i++) {
        ps.setInt(1, i + 1);
        ps.setString(2, lastNames[i]);
        ps.setString(3, firstNames[i]);
        // Add row to the batch.
        ps.addBatch();
    }
    // 3. Insert batch of data
    try {
        // Batch is ready, execute it to insert the data
        ps.executeBatch();
    } catch (SQLException e) {
        System.out.println("Error message: " + e.getMessage());
        return; // Exit if there was an error
    }

    connection.commit();
}
```
