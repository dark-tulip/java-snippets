### Enumerations
- способ ограничения возможных вариантов
- доп защита
- может иметь несколько конструкторов
- атрибуты конструктора должны быть определены если они не заданы
- конструктор приватный
- нельзя создать с new (не нуждается во внешнем вызове)
- имплементирует Интерфейсы Comparable, Serializable, Constable
- НЕЛЬЗЯ наследовать
- внутренне переводит в обычный класс
- Енумы нельзя клонировать, кидает CloneNotSupportedException - тем самым, являясь синглтонами
- EnumDesc описывает значение константы в енуме
- Номинальный дескриптор это имя присвоенное константе (*в Java номинальный тип определяется его именем*)
- constable (since Java 12) - описывает константное значение
- `describeConstable()` - представить себя в строковом значении (метод интерфейса Constable, для получения информации о классе во время исполнения)
- Несмотря на то что константы равны, разные классы енумов НЕЛЬЗЯ сравнивать, а equals() всегда будет false
- Нельзя переопределить метод `values()`
- Если хотите использовать enum в качестве ключа Множества или Мапы, используйте специальные мапы EnumSet, EnumMap

```Java

enum SeasonsReplica {
  SUMMER,
  AUTUMN,
  WINTER,
  SPRING;  
}

enum Seasons {
  SUMMER("Лето"),
  AUTUMN,
  WINTER("Зима"),
  SPRING("Весна");  // хороший тон заканчивать с ";"

  String value;

  // конструктор всегда должен быть private, НЕ может быть public
  Seasons(String acceptValue) {
    this.value = acceptValue;  // Для каждого еnum-a нужно будет задать значение в конструкторе
  }

  // можно задать пустой конструктор
  Seasons() {
  }

  // можно возвращать строкой другие значения
  public String getTextValue() {
    return value;
  }

}

public class EnumsEx {

  public static void main(String[] args) {
    System.out.println(Seasons.AUTUMN);
    System.out.println(Seasons.AUTUMN.value);  // null при не указанном значении
    System.out.println(Seasons.SPRING);
    System.out.println(Seasons.SPRING.getTextValue());
    System.out.println(Seasons.SPRING.value);
    System.out.println(Seasons.SPRING);
    System.out.println(Seasons.SPRING.compareTo(Seasons.WINTER));  // можно сравнивать по внутреннему ordinal, from 0 to Int Max value
    System.out.println("Seasons.SPRING.ordinal(): " + Seasons.SPRING.ordinal());
    System.out.println("Seasons.WINTER.ordinal(): " + Seasons.WINTER.ordinal());
    System.out.println("Seasons.SUMMER.ordinal(): " + Seasons.SUMMER.ordinal());
    System.out.println("Enum.valueOf(Seasons.class, \"SPRING\"): " + Enum.valueOf(Seasons.class, "SPRING"));
    System.out.println("Seasons.class.describeConstable(): " + Seasons.class.describeConstable().orElse(null));  // Seasons.SUMMER.describeConstable() ClassDesc[Seasons]
    System.out.println("Seasons.AUTUMN.describeConstable(): " + Seasons.AUTUMN.describeConstable().orElse(null));  // Seasons.SUMMER.describeConstable() EnumDesc[Seasons.AUTUMN]
//    System.out.println(Seasons.valueOf("sdf")); -> exception
    System.out.println(Seasons.valueOf(Seasons.AUTUMN.toString()));
    System.out.println(Seasons.SPRING.hashCode());
    System.out.println(Seasons.SPRING.value.hashCode());
    System.out.println(Seasons.AUTUMN.hashCode());
    System.out.println(Seasons.class.hashCode());
//    System.out.println(Seasons.valueOf(Seasons.AUTUMN.value));  // exception


//    System.out.println(Seasons.AUTUMN == SeasonsReplica.AUTUMN);              NOT ALLOWED!!!
    System.out.println(Seasons.AUTUMN.equals(SeasonsReplica.AUTUMN));           // always false
    System.out.println(Objects.equals(Seasons.AUTUMN, SeasonsReplica.AUTUMN));  // always false

    System.out.println(Arrays.toString(Seasons.values()));         // [SUMMER, AUTUMN, WINTER, SPRING]
    System.out.println(Arrays.toString(SeasonsReplica.values()));  // [SUMMER, AUTUMN, WINTER, SPRING]

    System.out.println((Seasons.values()));  // always false


    System.out.println(
      Arrays.toString(Seasons.values()).compareTo(Arrays.toString(SeasonsReplica.values()))  // TRUE!!!
    );
  }
}
```
