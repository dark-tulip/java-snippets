- DTO - хранить, сериализовывать, передавать данные
- Entity - сущность в БД без никакой логики
- DAO - сохранять и доставать данные в БД

Inversion of control - делегирование обязанностей внешнему компоненту

Одна из реализаций Inversion of control это dependency injection

Бин - объект класса управляемый контейнером бинов (ApplicationContext)
Подставить значение поля shop объекту seller
Аннотация компонент означает что класс который мы вызываем это бин
Аннотация autowired означает подставить значение, в поле которое оно аннотирует

Делегирование объектов класса спрингу
Когда мы передаем класс спрингу - он называется БИН
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


### Inversion of Control (Beans with xml)
```xml name='applicationContext.xml'
<?xml version="1.0" encoding="UTF-8"?>
<beans  xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <bean id="music" class="ClassicalMusic"/>
</beans>
```

```java
public interface Music {
  String sound();
}

public class ClassicalMusic implements Music {
  @Override
  public String sound() {
    return "PLAYING CLASSICAL MUSIC";
  }
}

public class PopMusic implements Music {
  @Override
  public String sound() {
    return "PlAYING POP MUSIC";
  }
}

public class MusicPlayer {
  Music music;
  
  // IoC
  public MusicPlayer(Music music) {
    this.music = music;
  }
  void playMusic() {
    System.out.println("Playing... " + music.sound());
  }
}

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        Music music = context.getBean("music", Music.class);

        MusicPlayer musicPlayer = new MusicPlayer(music);

        musicPlayer.playMusic();

        context.close();
    }
}
```

