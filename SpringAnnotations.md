- DTO - хранить, сериализовывать, передавать данные
- Entity - сущность в БД без никакой логики
- DAO - сохранять и доставать данные в БД

Inversion of control - делегирование обязанностей внешнему компоненту

Одна из реализаций Inversion of control это dependency injection

Бин - объект класса управляемый контейнером бинов (ApplicationContext)
Подставить значение поля shop объекту seller
Аннотация компонент означает что класс который мы вызываем это бин
Аннотация autowired означает подставить значение, в поле которое оно аннотирует


```Java

@Component
public class Shop {
}

@Component
public class Seller {
  @Autowired
  private Shop shop;
}
```

#### @Component

class is a front controller and responsible to handle user request and return appropriate response.

We can also specify the component name and then get it from spring context using the same name.

@Component("mc")
public class MathComponent {
}
MathComponent ms = (MathComponent) context.getBean("mc");

@Service and @Repository являются частными случаями of @Component.

#### @Repository
This annotation indicates that the class deals with CRUD operations
Обычно для определения репозитория базы данных (уровень доступа к БД)
Или определить что это DAO класс

#### @Service
Означает что класс содержит определенную бизнес логику
и отвечает за уровень сервиса
