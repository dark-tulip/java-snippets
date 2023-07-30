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
### Class Field
```Java
    Field classFld = clazz.getDeclaredField("livable");
    System.out.println("getType:           " + classFld.getType());
    System.out.println("getName:           " + classFld.getName());
    System.out.println("getDeclaringClass: " + classFld.getDeclaringClass());
    System.out.println("getAnnotatedType:  " + classFld.getAnnotatedType());
    System.out.println("getGenericType:    " + classFld.getGenericType());
    System.out.println("getModifiers:      " + classFld.getModifiers());
/*
getType:           interface org.example.ILivable
getName:           livable
getDeclaringClass: class org.example.Person
getAnnotatedType:  org.example.ILivable
getGenericType:    interface org.example.ILivable
getModifiers:      1
*/
```
***getFields VS getDeclaredFields**
- getFields - все публичные поля, и унаследованные публичные
- getDeclaredFields - все объявленные поля класса (public, private, protected, pkg-private); КРОМЕ УНАСЛЕДВАННЫХ;
```Java
// INTERFACE
public interface ILivable {
  String INTERFACE_FIELD = "here";
  void init();
}

// IMPLEMENTED CLASS
public class Person implements ILivable {

  public String    surname_public;
  private String   surname_private;
  protected String surname_protected;
  String           surname_pkg_default;
  public ILivable  livable = new PeronLivable();

  public void init() { }
}

// CALL METHODS
public class Reflections {
  public static void main(String[] args) {

    // includes public, protected, package-private and package access fields, EXCLUDED inherited
    System.out.println("-----getDeclaredFields: ");

    Field[] getDeclaredFields = Person.class.getDeclaredFields();
    Arrays.stream(getDeclaredFields).forEach(fld -> System.out.printf("%8s: %s%n", fld.getType(), fld.getName()));

    // все публичные поля; включая унаследованные (only public and public inherited)
    System.out.println("-----getFields: ");

    Field[] getFields = Person.class.getFields();
    Arrays.stream(getFields).forEach(fld -> System.out.printf("%8s: %s%n", fld.getType(), fld.getName()));
  }
}

// OUTPUT
/*
-----getDeclaredFields: 
class java.lang.String: surname_public
class java.lang.String: surname_private
class java.lang.String: surname_protected
class java.lang.String: surname_pkg_default
interface org.example.ILivable: livable
-----getFields: 
class java.lang.String: surname_public
interface org.example.ILivable: livable
class java.lang.String: INTERFACE_FIELD
*/
```

## Methods
- у примитивных типов тоже есть понятие класс
- при получении метода по имени обязательно нужно передать типы принимаетмых параметров
- **varagrs** `Method method = Person.class.getMethod("print", String[].class);` для метода `public String print(String... strs)`
- isDefault - проверить что использует реализацию по умолчанию от интерфейса (НЕ РАБОТАЕТ ДЛЯ АБСТРАКТНЫХ КЛАССОВ)

### Cинтетические методы 
- С Java 5 ДО Java 11
- создаются самим компилятором
- синтетические методы для обеспечения доступа ко внутреннему атрибуту
- поле должно быть приватным внутри вложенного класса
- для генерации синтетического метода атрибут должен использоваться - иначе сработает оптимизатоор и не создаст их
- 4096 modifier not declared in code, means synthetic метод
```Java
// 
public class Person {

  class NestedClass {
    private String value;
  }

  public String getNestedField() {
    return new NestedClass().value;
  }

  public void setNestedField(int nestedField) {
    new NestedClass().value = "" + nestedField;
  }
}

// CALL
public class Reflections {

  public static void main(String[] args) throws NoSuchMethodException {
    /**
     * Cинтетические методы (cтатические)
     * Method: access$000, isSynthetic: true
     * Method: access$002, isSynthetic: true
     */
    Method[] methods = Person.NestedClass.class.getDeclaredMethods();  
    for (Method m : methods) {
      System.out.println("Method: " + m.getName() + ", isSynthetic: " + m.isSynthetic() + ", \n" + m.toGenericString());
    }
}

//OUTPUT

// Method: access$000, isSynthetic: true,
// static java.lang.String org.example.Person$NestedClass.access$000(org.example.Person$NestedClass)

// Method: access$002, isSynthetic: true,
// static java.lang.String org.example.Person$NestedClass.access$002(org.example.Person$NestedClass,java.lang.String)
```
- знак доллара означает вложенный класс

### Modifiers
- класс Modifiers не содержит статического определения для синтетического метода
- Получить все модификаторы доступа метода `Modifier.toString(m.getModifiers())`
```Java
Modifier.PUBLIC = 1 = 0000 0001
Modifier.PRIVATE = 2 = 0000 0010
Modifier.PROTECTED = 4 = 0000 0100
Modifier.STATIC = 8 = 0000 1000
Modifier.FINAL = 16 = 0001 0000
Modifier.SYNCHRONIZED = 32 = 0010 0000
Modifier.VOLATILE = 64 = 0100 0000
Modifier.TRANSIENT = 128 = 1000 0000
Modifier.NATIVE = 256 = 1 0000 0000
Modifier.INTERFACE = 512 = 10 0000 0000
Modifier.ABSTRACT = 1024 = 100 0000 0000
```

