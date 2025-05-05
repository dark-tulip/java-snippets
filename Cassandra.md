Cassandra is example of `key value + column storage`
- CQL - cassandra query language
- simple HashMap
- бакет в виде связного списка
- у касандры декларативный язык запросов
- в bigTable каждый шард хранится в LSM (LSM for each machine) - log structured merge, sorted string tables - SSTable
- bloom filter используется для определения наличия значения в вероятностном порядке
- клиент может указывать желаемый ур консистентности
- `Consistent Hashing` - распределение ключей по диапазону в нодах (интервалы в кольце)

# Vector clocks
- предоставление пользователю на выбор временной ветки
- предоставление пользователю как выброр товара в корзине
- записи в распределенной системе
- Sx, Sy, Sz - это записи клиентов
- D1, D2 - это состояния
- D5 конечное состояние

<img width="406" alt="image" src="https://github.com/user-attachments/assets/fb27d4e8-648d-4587-8df6-58c3ef5292c2" />

# Merkle tree
1. Merkle root
2. Merkle branches
3. Merkle Leaves
4. Data nodes


- Кейспейс это аналог схемы в реляционных

```sql
SELECT * FROM system_schema.keyspaces;
```
```sql

CREATE KEYSPACE tansh28
  WITH REPLICATION = { 
   'class' : 'SimpleStrategy',
   'replication_factor' : 1 
  } 
AND DURABLE_WRITES = false ;;
   

select * from system_schema.columns where keyspace_name = 'tansh28';

CREATE TABLE tansh28.video ( 
   video_id    uuid, 
   added_date  timestamp, 
   description text, 
   title       text, 
   user_id     uuid,
   PRIMARY KEY ((video_id, added_date), user_id)
);

COPY video(video_id, added_date, description, title, user_id) FROM '/home/ifedotov/cassanda/sem1/videos.csv' WITH HEADER=true;
```




до двух млрд колонок
`compound primary keys`
- `1. Partition key` - как будут распределены
- `2. Clustering key` - как будут отсортированы
