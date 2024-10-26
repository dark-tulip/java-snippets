- в математике любая функция возвращающая число это хэш функция
- массив это **direct addressing**
- когда два ключа дают один и тот же хэш код это коллизии
- chaining, в одной ячейке (бакете, позволяет хранить несколько значений)

# ClassCastException
- `map.values()` возвращает коллекцию и эту коллекцию нельзя просто так преобразовать в список
- внутренний класс Values хэш мапы extends от абстрактной коллекции и НЕ ИМПЛЕМЕНТИРУЕТ интерфейс `List`. Вот почему `ClassCastException`
- `ArrayList` в конструкторе принимает коллекцию, и через нее можно смапить `new ArrayList<>(map.values())`
  
```java
    public static void main(String[] args) {
        // так ClassCastException
        // Exception in thread "main" java.lang.ClassCastException: java.util.HashMap$Values cannot be cast to java.util.List
        System.out.println(cast());
    }

    public static List<Integer> cast() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("test", 123);
        map.put("test2", 1234);
        map.put("test3", 1235);

        return (List<Integer>) map.values();
    }
```


- если отсортированную мапу приравнять к хэшмапе - результат сортировки сотрется
- use LinkedList to keep order
```Java
    HashMap<Integer, String> mapa = new HashMap<>();
    mapa.put(1, "4val");
    mapa.put(2, "1vall");
    mapa.put(3, "6val");
    mapa.put(4, "2value");

    LinkedHashMap<Integer, String> mapa2 = mapa.entrySet()
                                               .stream()
                                               .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(String::length)))
                                               .collect(Collectors.toMap(Map.Entry::getKey,
                                                                         Map.Entry::getValue,
                                                                         (o1, o2) -> o1,
                                                                         LinkedHashMap::new));
    System.out.println(mapa2);

    // {1=4val, 3=6val, 2=1vall, 4=2value}
```
