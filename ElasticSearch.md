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


# Install

```bash
# download bin source (Download and unzip Elasticsearch)
cd elasticsearch-8.10.2
bin/elalstic
```

```
ℹ️  Password for the elastic user (reset with `bin/elasticsearch-reset-password -u elastic`):
  H_LXunj50FtrCCGYHv38
ℹ️  HTTP CA certificate SHA-256 fingerprint:
  271fa7b1efe122fb616464299e9c11805dc17be16fe013aaf2b183bb57e3bbc3
ℹ️  Configure Kibana to use this cluster:
• Run Kibana and click the configuration link in the terminal when Kibana starts.
• Copy the following enrollment token and paste it into Kibana in your browser (valid for the next 30 minutes):
eyJ2ZXIiOiI4LjEwLjIiLCJhZHIiOlsiMTkyLjE2OC4xLjE4NTo5MjAwIl0sImZnciI6IjI3MWZhN2IxZWZlMTIyZmI2MTY0NjQyOTllOWMxMTgwNWRjMTdiZTE2ZmUwMTNhYWYyYjE4M2JiNTdlM2JiYzMiLCJrZXkiOiIyeXBaLW9vQk90TWFIajRQVldpczp2RW5ISzJ5OFNVMmxmMHYxeXBnTFRnIn0=
```
