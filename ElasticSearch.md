# Elasticsearch 

- distributed data storage, which storages data on json format
- При создании инстанции эластика создается его нода, буквально нода это хост, на котором запущен экземпляр эластика
- Коллекция запущенных нод называется кластером
- NoSQL документоориентированная бд
- Для поиска использует инвертированный индекс который вычисляет каждое уникальное слово в документе
- 

В чем особенность эластика?
- это масштабирование системы, в том плане что ее можно расширять in live режиме
- система высоконагружена, используется в поисковых системах, отчетах, логировании, анализе данных (ML)

Индекс это database in elastic
Mapping это аналог таблицы

## ElasticStack
# Kibana
- data analyzis and visualization platform
# Logstash
- An event processing pipeline
- input plugins -> filter plugins -> outputs
# X-pack - elastic extension tools
- security (authZ, authZ by AD or etc)
- notification about CPU usage
- enables ML
- Reporting data in csv, pdf
- forecasting for scaling
