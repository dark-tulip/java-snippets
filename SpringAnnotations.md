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
#### Автоматическое внедрение зависимостей
1. Внедрение зависимостей с помощью конструктора и конфигурацией xml 
```Java
/** ApplicationContext.xml - injection with constructor */
    <bean id="music" class="ClassicalMusic"/>
    <bean id="musicPlayer" class="MusicPlayer">
        // Внедрили ссылку на другой бин
        <constructor-arg ref="music"/>  // Доверили спрингу внедрение зависимости, полученный бин будет с внедренной зависимостью
    </bean>
    
/** Main.java */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //  Music music = context.getBean("music", Music.class);
        //  MusicPlayer musicPlayer = new MusicPlayer(music);
        //  Usage is:
        MusicPlayer musicPlayer = context.getBean("musicPlayer", MusicPlayer.class);
        musicPlayer.playMusic();  // Playing... PLAYING CLASSICAL MUSIC
        context.close();
    }
```
2. С помощь сеттера
```Java
public class MusicPlayer {
  Music music;

  // IoC
  public MusicPlayer(Music music) {
    this.music = music;
  }

  // SETTER needs default constructor
  public MusicPlayer() { }
  
  // Setter
  public void setMusic(Music music) {
    this.music = music;
  }

  void playMusic() {
    System.out.println("Playing... " + music.sound());
  }
}
```
SetMusic в качестве аргумента передает ранее созданный бин
```xml
    <bean id="musicType" class="ClassicalMusic"/>
    <!--
        Создает объект с пустым конструктором MusicPlayer
        с помощью метода set music назначает этому объекту зависимость music bean
    -->
    <bean id="musicPlayer" class="MusicPlayer">
        <!-- setSong, setMusic-->
        <property name="music" ref="musicType"/>
    </bean>
```
Вставка значений
```xml
    <bean id="musicPlayer" class="MusicPlayer">
        <property name="music" ref="musicType"/>
        <property name="name" value="Some name"/>
        <property name="volume" value="100"/>
    </bean>
```
``` Java
public class MusicPlayer {
  Music music;
  int volume;
  String name;
  // IoC
  public MusicPlayer(Music music) {
    this.music = music;
  }

  // SETTER needs default constructor
  public MusicPlayer() { }

  // Setter
  public void setMusic(Music music) {
    this.music = music;
  }

  void playMusic() {
    System.out.println("Playing... " + music.sound());
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```
```
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//        Music music = context.getBean("music", Music.class);
//        MusicPlayer musicPlayer = new MusicPlayer(music);
        MusicPlayer musicPlayer = context.getBean("musicPlayer", MusicPlayer.class);
        musicPlayer.playMusic();  // Playing... PLAYING CLASSICAL MUSIC
        context.close();

//        Playing... PLAYING CLASSICAL MUSIC
//        Some name
//        100
        System.out.println(musicPlayer.getName());
        System.out.println(musicPlayer.getVolume());
    }
```
Задать значения с помощью classpath
```
    <context:property-placeholder location="classpath:musicPlayer.properties"/>
    <bean id="musicType" class="ClassicalMusic"/>
    <bean id="musicPlayer" class="MusicPlayer">
        <property name="music" ref="musicType"/>
        <property name="name" value="${musicPlayer.name}"/>
        <property name="volume" value="${musicPlayer.volume}"/>
    </bean>
```
```
// musicPlayer.properties
musicPlayer.name=Some value
musicPlayer.volume=1799
```
result
```
Playing... PLAYING CLASSICAL MUSIC
Some value
1799
```
#### Scopes
```
scope создает то, как spring будет создавать бины
Scope singleton - указывет на один и тот же участок в памяти, на один и тот же бин
```
