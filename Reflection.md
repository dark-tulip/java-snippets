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
### Field
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
**getFields VS getDeclaredFields**
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

// CALL fields
public class Reflections {
  public static void main(String[] args) {

    // includes public, protected, package-private and package access fields, EXCLUDED inherited
    Field[] getDeclaredFields = Person.class.getDeclaredFields();

    // все публичные поля; включая унаследованные (only public and public inherited)
    Field[] getFields         = Person.class.getFields();

    System.out.println("-----getDeclaredFields: ");
    Arrays.stream(getDeclaredFields).forEach(fld -> System.out.printf("%8s: %s%n", fld.getType(), fld.getName()));

    System.out.println("-----getFields: ");
    Arrays.stream(getFields).forEach(fld -> System.out.printf("%8s: %s%n", fld.getType(), fld.getName()));
  }
}
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
