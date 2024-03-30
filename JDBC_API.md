PreparedStatement - это общий протокол, a JDBC-API это общий интерфейс

JDBC API состоит из 
1) Simple query
2) Extended query

## Statement mode and Transaction mode
- Statement mode обработает только один запрос 
- Transaction mode целую транзакцию (в транзакции может быть несколько запросов)
