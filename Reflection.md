- работает в процессе рантайма
- помогает получить инфу и о данных класса, методах, или полей
- примитивные типы также представлены в виде объектов

### Три способа получения объекта класса класс
```Java
Class clazz = Person.class;
Class clazz2 = new Person().getClass();
Class clazz3 = Class.forName("org.example.Person");

System.out.println(clazz + " " + clazz2  + " " + clazz3);
// class org.example.Person class org.example.Person class org.example.Person
```
