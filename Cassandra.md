Cassandra is example of key value storage
- simple HashMap
- бакет в виде связного списка
- у касандры декларативный язык запросов
- в bigTable каждый шард хранится в LSM (LSM for each machine) - log structured merge, sorted string tables - SSTable
- bloom filter используется для определения наличия значения в вероятностном порядке

- `Consistent Hashing` - распределение ключей по диапазону в нодах (интервалы в кольце)
