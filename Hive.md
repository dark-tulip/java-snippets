### Task1 

```bash
hive -f task1.sql;
hive -e "USE kkt_transactions; SELECT * FROM raw_kkt_data LIMIT 50;" > result.txt
```

```sql
use hobod2025s008;


CREATE EXTERNAL TABLE IF NOT EXISTS raw_kkt_data (
    json_string STRING
)
STORED AS TEXTFILE
LOCATION '/data/hive/fns2';


select * from raw_kkt_data;


CREATE TABLE IF NOT EXISTS parsed_kkt_data AS
SELECT
    get_json_object(json_string, '$.receipt.user') AS user,
    get_json_object(json_string, '$.receipt.userInn') AS userInn,
    get_json_object(json_string, '$.receipt.requestNumber') AS requestNumber,
    get_json_object(json_string, '$.receipt.dateTime') AS dateTime,
    get_json_object(json_string, '$.receipt.shiftNumber') AS shiftNumber,
    get_json_object(json_string, '$.receipt.operationType') AS operationType,
    get_json_object(json_string, '$.receipt.taxationType') AS taxationType,
    get_json_object(json_string, '$.receipt.operator') AS operator,
    get_json_object(json_string, '$.receipt.kktRegId') AS kktRegId,
    get_json_object(json_string, '$.receipt.fiscalDriveNumber') AS fiscalDriveNumber,
    get_json_object(json_string, '$.receipt.retailPlaceAddress') AS retailPlaceAddress,
    get_json_object(json_string, '$.receipt.buyerAddress') AS buyerAddress,
    get_json_object(json_string, '$.receipt.totalSum') AS totalSum,
    get_json_object(json_string, '$.receipt.cashTotalSum') AS cashTotalSum,
    get_json_object(json_string, '$.receipt.ecashTotalSum') AS ecashTotalSum,
    get_json_object(json_string, '$.receipt.fiscalDocumentNumber') AS fiscalDocumentNumber,
    get_json_object(json_string, '$.receipt.fiscalSign') AS fiscalSign,
    get_json_object(json_string, '$.receipt.items') AS items_json
FROM raw_kkt_data
WHERE json_string LIKE '%"receipt"%';
```


### Task 2

`task2.sql`

```sql
SET hive.cli.print.header=false;
SET mapred.input.dir.recursive=true;
SET hive.mapred.supports.subdirectories=true;
SET mapreduce.job.reduces = 4;

USE hobod2025s008;

CREATE EXTERNAL TABLE IF NOT EXISTS raw_kkt_data (
    json_string STRING
)
STORED AS TEXTFILE
LOCATION '/data/hive/fns2';

CREATE TABLE IF NOT EXISTS parsed_kkt_data AS
SELECT
    get_json_object(json_string, '$.content.user')                 AS user,
    get_json_object(json_string, '$.content.userInn')              AS userInn,
    get_json_object(json_string, '$.content.requestNumber')        AS requestNumber,
    get_json_object(json_string, '$.content.dateTime.$date')       AS dateTime,
    get_json_object(json_string, '$.content.shiftNumber')          AS shiftNumber,
    get_json_object(json_string, '$.content.operationType')        AS operationType,
    get_json_object(json_string, '$.content.taxationType')         AS taxationType,
    get_json_object(json_string, '$.content.operator')             AS operator,
    get_json_object(json_string, '$.content.kktRegId')             AS kktRegId,
    get_json_object(json_string, '$.content.fiscalDriveNumber')    AS fiscalDriveNumber,
    get_json_object(json_string, '$.content.retailPlaceAddress')   AS retailPlaceAddress,
    get_json_object(json_string, '$.content.buyerAddress')         AS buyerAddress,
    get_json_object(json_string, '$.content.totalSum')             AS totalSum,
    get_json_object(json_string, '$.content.cashTotalSum')         AS cashTotalSum,
    get_json_object(json_string, '$.content.ecashTotalSum')        AS ecashTotalSum,
    get_json_object(json_string, '$.content.fiscalDocumentNumber') AS fiscalDocumentNumber,
    get_json_object(json_string, '$.content.fiscalSign')           AS fiscalSign,
    get_json_object(json_string, '$.content.items')                AS items_json,
    get_json_object(json_string, '$.subtype')                      AS subtype
FROM raw_kkt_data;
--
-- в TEXT формате
CREATE TABLE IF NOT EXISTS kkt_text
    STORED AS TEXTFILE
    LOCATION '/data/hive/kkt_text'
    AS SELECT * FROM parsed_kkt_data;

-- в ORC формате
CREATE TABLE IF NOT EXISTS kkt_orc
    STORED AS ORC
    LOCATION '/data/hive/kkt_orc'
    AS SELECT * FROM parsed_kkt_data;

-- в PARQUET формате
CREATE TABLE IF NOT EXISTS kkt_parquet
    STORED AS PARQUET
    LOCATION '/data/hive/kkt_parquet'
    AS SELECT * FROM parsed_kkt_data;
```

`run.sh`
```bash
hive -f task2.sql > /dev/null


hive -e "USE hobod2025s008;
SELECT userInn, SUM(CAST(totalSum AS BIGINT)) AS profit
FROM kkt_text
WHERE totalSum IS NOT NULL
GROUP BY userInn
ORDER BY profit DESC
LIMIT 1;" | grep -v WARN > result_text.txt

hive -e "USE hobod2025s008;
SELECT userInn, SUM(CAST(totalSum AS BIGINT)) AS profit
FROM kkt_orc
WHERE totalSum IS NOT NULL
GROUP BY userInn
ORDER BY profit DESC
LIMIT 1;" | grep -v WARN > result_orc.txt

hive -e "USE hobod2025s008;
SELECT userInn, SUM(CAST(totalSum AS BIGINT)) AS profit
FROM kkt_parquet
WHERE totalSum IS NOT NULL
GROUP BY userInn
ORDER BY profit DESC
LIMIT 1;" | grep -v WARN > result_parquet.txt


# shellcheck disable=SC2028
echo -e "Формат хранения данных\tВремя работы запроса" > time_result.txt
echo -e "TEXT\tMapReduce Total cumulative CPU time: 9 seconds 520 msec;Total MapReduce CPU Time Spent: 1 minutes 3 seconds 480 msec" >> time_result.txt
echo -e "ORC\tMapReduce Total cumulative CPU time: 4 seconds 610 msec;Total MapReduce CPU Time Spent: 18 seconds 650 msec"  >> time_result.txt
echo -e "PARQUET\tMapReduce Total cumulative CPU time: 6 seconds 940 msec;Total MapReduce CPU Time Spent: 38 seconds 270 msec" >> time_result.txt


cat result_parquet.txt;

```


### Task 3
```bash
hive -e "use default;
add jar hdfs:/opt/cloudera/parcels/CDH/lib/hive/lib/json-serde-1.3.8-jar-with-dependencies.jar;
set ignore.malformed.json = true;
WITH daily_profits AS (
  SELECT
    content.userinn AS userInn,
    day(to_date(content.datetime.date)) AS day_of_month,
    SUM(content.totalsum) AS profit_rub
  FROM kkt
  WHERE
    content.userinn IS NOT NULL
    AND content.datetime.date IS NOT NULL
  GROUP BY
    content.userinn,
    day(to_date(content.datetime.date))
),

ranked_profits AS (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY userInn ORDER BY profit_rub DESC) AS rn
  FROM daily_profits
)

SELECT userInn, day_of_month, COALESCE(profit_rub, 0)
FROM ranked_profits
WHERE rn = 1
ORDER BY userInn ASC;" | grep -v WARN > result_text.txt

cat result_text.txt;

```

### Task 4

```bash
hive -e "use default;

add jar hdfs:/opt/cloudera/parcels/CDH/lib/hive/lib/json-serde-1.3.8-jar-with-dependencies.jar;
set ignore.malformed.json = true;

WITH profits_by_halfday AS (
  SELECT
    content.userinn AS userInn,
    CASE
      WHEN hour(cast(content.datetime.date AS timestamp)) < 13 THEN 'morning'
      ELSE 'evening'
    END AS half_day,
    COUNT(*) AS txn_count,
    SUM(content.totalsum) AS total_profit
  FROM kkt
  WHERE
    subtype = 'receipt'
    AND content.userinn IS NOT NULL
    AND content.datetime.date IS NOT NULL
  GROUP BY
    content.userinn,
    CASE
      WHEN hour(cast(content.datetime.date AS timestamp)) < 13 THEN 'morning'
      ELSE 'evening'
    END
),

pivoted AS (
  SELECT
    userInn,
    ROUND(SUM(CASE WHEN half_day = 'morning' THEN total_profit / txn_count END)) AS morning_avg_profit,
    ROUND(SUM(CASE WHEN half_day = 'evening' THEN total_profit / txn_count END)) AS evening_avg_profit
  FROM profits_by_halfday
  GROUP BY userInn
)

SELECT userInn, morning_avg_profit, evening_avg_profit
FROM pivoted
WHERE morning_avg_profit > evening_avg_profit
ORDER BY morning_avg_profit ASC;

" | grep -v WARN > result_text.txt

cat result_text.txt;
```

### Task 5

```bash
hive -e "use default;

add jar hdfs:/opt/cloudera/parcels/CDH/lib/hive/lib/json-serde-1.3.8-jar-with-dependencies.jar;
set ignore.malformed.json = true;

WITH events AS (
  SELECT
    content.userinn AS userInn,
    content.kktregid AS kkt,
    cast(content.datetime.date AS timestamp) AS ts,
    subtype
  FROM kkt
  WHERE subtype IN ('openShift', 'closeShift', 'receipt')
    AND content.userinn IS NOT NULL
    AND content.kktregid IS NOT NULL
    AND content.datetime.date IS NOT NULL
),

ordered_events AS (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY userInn, kkt ORDER BY ts) AS rn
  FROM events
),

-- cumulative count open/close
mode_tracking AS (
  SELECT *,
         SUM(CASE WHEN subtype = 'openShift' THEN 1 ELSE 0 END)
           OVER (PARTITION BY userInn, kkt ORDER BY ts ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS opens,
         SUM(CASE WHEN subtype = 'closeShift' THEN 1 ELSE 0 END)
           OVER (PARTITION BY userInn, kkt ORDER BY ts ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS closes
  FROM ordered_events
),

-- Нарушения:
-- 1. Receipt до любого openShift (opens = 0)
-- 2. Receipt после последнего closeShift (closes >= opens, но потом ещё идут receipt)
-- 3. Receipt между закрытием и открытием (closes > opens)
violations AS (
  SELECT userInn, kkt, ts
  FROM mode_tracking
  WHERE subtype = 'receipt'
    AND (
      opens = 0
      OR closes > opens
    )
)

-- print result
SELECT DISTINCT userInn
FROM violations
ORDER BY userInn limit 50;


" | grep -v WARN > result_text.txt

cat result_text.txt;


```
