### Про построение Spring-а
Author: Евгений Борисов https://youtu.be/rd6wxPzXQvo

Code should be (правила кодаписания)
- Flexible
- Reausable
- Readable

Java class loader не умеет относиться к версиям, возьмет первый попавшийся jar
SOLID - концепция возникла позже языка
- помогает сделать код гибче
раньше код писался один раз и навсегда, не было понятия версионности

#### Single responsibility
- Какую имплементацию выбрать
- Как создать эту имплементацию
- Как настроить эту имплементацию
- Приватные методы

Кто решает какая имплементация?
- Factory (с начала 2000х) - ObjectFactory на все случаи жизни
- Сервисы по бильнишнству своему синглтоны
- Дженерик определяется во время runtime-а 

Factory 
- Централизованное место для создания всех объектов
- Перед тем как фабрика создаст объект мы можем его настроить с помощью конфига
- Фабрика должна отвечать за создание объектов

</br>

- Объекты типа сервисы лучше всего делать stateless
Stateless - переводчик.. нету стэйта - это полна чушь, когда сервис при создании настроен и создался его стейт уже потом меняться не будет, состояние никогда не изменится - понятие синглтона, практически все сервисы синглтоны то соответственно стейтлессы (не нужно каждый раз возращять клон объекта)

</br>

- Нужны синглтоны,
- Когда обхект функциональный
- Когда создание обхекта будет бить по перформансу
- Когда объект сервис

</br>

- lookup появился до Инверсии контроля 
- (object factory) Домашние синглтоны это плохо - каждый синглтон дергает фабрику объектов - все объекты связаны статическими обхектами getInstance
- Все дергают кастомную фабрику через статический метод - а фабрика создается медленно (для прода один раз - ничего НО) - каждый тест будет убивать перформанс
- проблема при юнит тестах - дергается фабрика
- actual for 2003 year
- утечка инфраструктуры внутри бизнес логики


```Java
package com.epam;

public class RecommendatorImpl implements Recommendator {
  @InjectProperty(value = "AAA")
  private String alcohol;

  @Override
  public void recommend() {
    System.out.println("To save from corona drink, " + alcohol);
  }
}
```
O - Система открыта для изменений но закрыта для изменения текущей логики
```Java
package com.epam;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class ObjectFactory {
  private static ObjectFactory ourInstance = new ObjectFactory();
  private Config config;

  public static ObjectFactory getInstance() {
    return ourInstance;
  }

  private ObjectFactory() {
    config = new JavaConfig("com.epam", new HashMap<>(Map.of(Policeman.class, PolicemanImpl.class)));
  }

  @SneakyThrows  // сделать checked exception unchecked-ом
  public <T> T createObject(Class<T> type) {
    Class<? extends T> implClass = type;
    if (type.isInterface()) {
      implClass = config.getImplClass(type);
    }

    T t = implClass.getDeclaredConstructor().newInstance();

    for (Field field : t.getClass().getDeclaredFields()) {
      InjectProperty annotation = field.getAnnotation(InjectProperty.class);

      // создать мапу из ресурс файла application.properties
      String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
      Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
      Map<String, String> properties = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));

      if (annotation != null) {
        String value;
        if (annotation.value().isEmpty()) {  // если аннотация пустая берем значение из мапы
          value = properties.get(field.getName());
        } else {
          value = annotation.value();
        }
        field.setAccessible(true);
        field.set(t, value);  // этот филд не статический, принимает объект филда в который нужно вставить значение
      }
    }
    return t;
  }
}
```

- Don't call us we we'll call you
- IoC - нельзя больше создавать обхект через new
- Нельзя воспитать ребенка пока он не родился - то же что и 
- Нельзя настроить объект пока он не создался
- Тут возникает second face constructor - init method (который помечается как @PostConstruct) - который надо прогнать после того как обхект уже настроен

##### ProxyPattern
- Подмена объекта на другой соответствующий по типу
- И его подмену никто не заметит, а фабрика вместо того чтобы выбрать какой то объект вернет прокси
- Есть API в джаве встроенный, который называется - DynamicProxy 
- Есть CGLIB - который делает прокси с помощью наследования
- с 99 года библиотека которая дает возможность создать на лету класс (прокси класс) и рассказать какие интерфейсы он должен имплементировать
- ProxyClass - который генерируется на основе нашего интерфейса на лету

##### Итог, как спринг создает объекты
```
 @SneakyThrows  // сделать checked exception unchecked-ом
  public <T> T createObject(Class<T> implClass) {

    // создает метод
    T t = create(implClass);

    // настраивает
    configure(t);

    // донастраивает like BeanPostProcessor
    invokeInit(implClass, t);

    t = replaceWithProxyIfNeeded(implClass, t);

    // Возращает настроенный объект
    return t;
  }
```
