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
