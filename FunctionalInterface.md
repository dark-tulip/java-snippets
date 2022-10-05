## Реализация функционального интерфейса на java

- Функциональные интерфейсы нужны для создания анонимных классов с одним абстрактным методом
- From Java 8
- @FunctionalInterface - сообщить компилятору что может быть только один метод (просто доп страховка)
- Вместе достпуны статические и дефолтные методы
- Только один метод чтобы реализовать лямбду
- foreach, аналогичен методу peek, разница в том, что он конечный — терминальный.

```
- <T> – the type of the input to the function 
- <R> – the type of the result of the function
- <E> – the type of elements held in this deque
- <K> – the type of keys maintained by this map 
- <V> – the type of mapped values
- X – if no value is present
- <N> - node we are pointing to
```
