```
docker run --name acid -d -e POSTGRES_PASSWORD=postgres postgres:13
docker exec -it acid psql -U postgres
```

```sql
CREATE TABLE products(
  pid serial primary key,
  name text,
  price float,
  inventory integer
);

CREATE TABLE sales(
  pid integer,
  price float,
  quantity integer
);

INSERT INTO products(name, price, inventory) VALUES ('phone', 999.99, 100);
```
```bash
# clear screen hotkey
control + option + L
```
Начали транзакцию и вышли (к примеру произошел сбой)

<img width="548" alt="image" src="https://user-images.githubusercontent.com/89765480/226115870-86a39a76-64c1-474b-8eba-3ff6443b1f75.png">

```
begin transaction;
select * from products;
update products set inventory = inventory - 10;
select * from products;
exit;
```
транзакция откатилась, данные остались без изменений
<img width="683" alt="image" src="https://user-images.githubusercontent.com/89765480/226116017-fb8790e3-4058-4489-9661-de345fbce8ef.png">

A - atomicity - транзакция выполнится целиком или не выполнится вообще (выполнится вся последовательность комманд в рамках данной транзакции - тобишь не делимость)

Пока транзкакция не зафиксирована - наша база находится в НЕ-консистентном состоянии

<img width="1363" alt="image" src="https://user-images.githubusercontent.com/89765480/226117321-4221bf27-cd8e-4e0e-a45c-bd535939463b.png">

После коммита данные синхронизиировались

<img width="1365" alt="image" src="https://user-images.githubusercontent.com/89765480/226117659-d3dddf94-2277-41f3-b600-cf207e12afe8.png">

При возникновении любой ошибки вся транзакция откатывается - в данном случае появился ! знак и вместо коммита произошел ROLLBACK

<img width="625" alt="image" src="https://user-images.githubusercontent.com/89765480/226118684-1b06a3a1-844c-4dbd-ae27-2b6adf21c9f7.png">


### Консистентность

```
insert into products(name, price, inventory) values ('headphone',99.99,80);
insert into sales(pid, price, quantity) values (1, 999.99, 10);
insert into sales(pid, price, quantity) values (1, 999.99, 5);
insert into sales(pid, price, quantity) values (2, 99.99, 5);
insert into sales(pid, price, quantity) values (2, 89.99, 15);
insert into sales(pid, price, quantity) values (2, 79.99, 25);
```

<img width="638" alt="image" src="https://user-images.githubusercontent.com/89765480/226119115-06922003-838e-4d2c-8e6f-2c2a9e444c5d.png">

По умолчанию установленный в постгресе уровень изоляции - read-committed который не защищает от фантомного чтения, когда другая транзакция закоммитила данные и в процессе выборки возникла фантомная запись

к примеру было в транзакции11

<img width="581" alt="image" src="https://user-images.githubusercontent.com/89765480/226121164-4500a687-7dd0-4a82-a5a5-27a40ddc3cb2.png">

затем транзакция22 закоммитила данные

<img width="666" alt="image" src="https://user-images.githubusercontent.com/89765480/226121188-f6ab3171-3bc4-4b17-9052-994fd037e363.png">

тем временем та же выборка в транзакции11 - что не совсем очевидно

<img width="567" alt="image" src="https://user-images.githubusercontent.com/89765480/226121236-12c95c34-757f-4e13-8206-5d871011a217.png">

Уровень изоляции repeatable-read - делает некий снапшот таблицы в рамках данной транзакции

<img width="1352" alt="image" src="https://user-images.githubusercontent.com/89765480/226121583-64cd7d70-1f01-424a-a8da-1485952184a6.png">

изменения других транзакций будут видны только после коммита текущей транзакции

<img width="692" alt="image" src="https://user-images.githubusercontent.com/89765480/226122081-8fd8be39-1ca9-49c0-b875-f5ed7a6ed447.png">

Устойчивость

резкий шатдаун базы данных

<img width="1404" alt="image" src="https://user-images.githubusercontent.com/89765480/226122541-15602d13-8d60-4baf-8139-4e152b6aae2c.png">

Данные зафиксировались - транзакция устойчива (коммит зафиксирован)
