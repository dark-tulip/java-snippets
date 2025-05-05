Cassandra is example of key value storage
- simple HashMap
- бакет в виде связного списка
- у касандры декларативный язык запросов
- в bigTable каждый шард хранится в LSM (LSM for each machine) - log structured merge, sorted string tables - SSTable
- bloom filter используется для определения наличия значения в вероятностном порядке

- `Consistent Hashing` - распределение ключей по диапазону в нодах (интервалы в кольце)

# Vector clocks
- предоставление пользователю на выбор временной ветки
- предоставление пользователю как выброр товара в корзине
- записи в распределенной системе
- Sx, Sy, Sz - это записи клиентов
- D1, D2 - это состояния
- D5 конечное состояние

<img width="406" alt="image" src="https://github.com/user-attachments/assets/fb27d4e8-648d-4587-8df6-58c3ef5292c2" />
