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
- для генерации синтетического метода атрибут должен использоваться - иначе сработает оптимизатоор и не создаст их (генерируется только то что используется)
- класс верхнего уровня генерирует синтетический метод
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

### Bridge methods Java
- Мостовые методы на Java
- связаны со стиранием типов
- дженерики проверяются во время компиляции
- чтобы создать дженерики компилятор использует стирание типов для
    - если не объявлен конкретный тип данных для дженерика - кастит в объекты, и компилирует байт код в виде классов Object
    - создает мостовые методы для обеспечения полиморфизма
- параметрзированные типы на Java - это дженерики, позволяющие создать обобщенный универсальный класс, работающий с разными типами данных

```Java
class GenericNode<T> {
  T value;
  public T getValue() {
    return value;
  }
  public void setValue(T val) {
    this.value = val;
  }
}

class GenericNodeImpl extends GenericNode<Integer> {
  public Integer getValue() {
    return value;
  }
}

public class Reflections {

  public static void main(String[] args) throws NoSuchMethodException {

    GenericNodeImpl ll = new GenericNodeImpl();
    Method[] methods = ll.getClass().getDeclaredMethods();
    for (Method m : methods) {
      System.out.printf("m.getName: %-10s is bridge: %-7s getReturnType: %-25s %-30s%n",  m.getName(), m.isBridge(), m.getReturnType(), Arrays.toString(m.getParameterTypes()));

    }
}

/*
m.getName: getValue   is bridge: false   getReturnType: class java.lang.Integer   []                            
m.getName: getValue   is bridge: true    getReturnType: class java.lang.Object    []  
*/
```
- getDeclaredMethods показывает мостовые методы тоже

### Default methods Java
- переопределенные дефолтные методы с интерфейса уже не являются дефолтными
```Java
// INTERFACE
interface IMovalble {
    default String move(int pos) {
        return "moved: " + pos;
    }

    default String move2(int pos) {
        return "moved interface: " + pos;
    }
}


// IMPL CLASS
class Car implements IMovalble {
    @Override
    public String move2(int pos) {
        return "moved2 interface: " + pos;
    }
}

// CALL
public class Test {
    public static void main(String[] args) {
        for(var m :  new Car().getClass().getMethods()) {
            System.out.printf("name: %-10s isdefault: %-10s return type: %-10s%n", m.getName(), m.isDefault(), m.getReturnType().getSimpleName());
        }
    }
}

/*
name: move2      isdefault: false      return type: String - Overriden - not default    
name: wait       isdefault: false      return type: void      
name: wait       isdefault: false      return type: void      
name: wait       isdefault: false      return type: void      
name: equals     isdefault: false      return type: boolean   
name: toString   isdefault: false      return type: String    
name: hashCode   isdefault: false      return type: int       
name: getClass   isdefault: false      return type: Class     
name: notify     isdefault: false      return type: void      
name: notifyAll  isdefault: false      return type: void      
name: move       isdefault: true       return type: String - NOT OVERRIDEN, still default  
*/
Абстрактные методы, реализованные в интерфейсе
```
### IsVarArgs методы с переменным кол-вом аргументов

// CLASS
```Java
public class Person {
  public String print(String... strs) {
    return Arrays.toString(strs);
  }
}


// CALL
public class Reflections {
  public static void main(String[] args) throws NoSuchMethodException {
    Method method = Person.class.getMethod("print", String[].class);

    System.out.println("getName: " + method.getName());
    System.out.println("getDeclaringClass: " + method.getDeclaringClass().getSimpleName());
    System.out.println("getReturnType: " + method.getReturnType().getSimpleName());
    System.out.println("getModifiers: " + method.getModifiers());
    System.out.println("getModifiers isTransient: " + Modifier.isTransient(method.getModifiers()));
    System.out.println("getModifiers isPublic: " + Modifier.isPublic(method.getModifiers()));
    System.out.println("getModifiers toString: " + Modifier.toString(method.getModifiers()));
    System.out.println("getAnnotatedReturnType: " + method.getAnnotatedReturnType().getType());
    System.out.println("getExceptionTypes: " + method.getExceptionTypes().getClass().getSimpleName());
    System.out.println("getParameterCount: " + method.getParameterCount());
    System.out.println("getParameterTypes: " + Arrays.toString(method.getParameterTypes()));
    System.out.println("isVarArgs: " + method.isVarArgs());
    System.out.println("isBridge: " + method.isBridge());
    System.out.println("isSynthetic: " + method.isSynthetic());
    System.out.println("isDefault: " + method.isDefault());
    System.out.println("toGenericString: " + method.toGenericString());

  }
}

// OUTPUT
/*
getName: print
getDeclaringClass: Person
getReturnType: String
getModifiers: 129
getModifiers isTransient: true
getModifiers isPublic: true
getModifiers toString: public transient
getAnnotatedReturnType: class java.lang.String
getExceptionTypes: Class[]
getParameterCount: 1
getParameterTypes: [class [Ljava.lang.String;]
isBridge: false
isSynthetic: false
isDefault: false
isVarArgs: true
toGenericString: public java.lang.String org.example.Person.print(java.lang.String...)
*/
```
#### Varargs
- методы с переменной длиной аргументов
- должны быть на конце списка аргументов метода
- heap pollution problem (Class Cast Exception) https://www.baeldung.com/java-varargs
- модификатор в методе isVARARGS имеет такое же значение как и в классе Modifiers.isTRANSIENT - ЭТО НИКАКОГО ОТНОШЕНИЯ НЕ ИМЕЕТ
```Java
System.out.println("Modifier: " + Modifier.toString(-1));

// OUTPUT
// Modifier: public protected private abstract static final transient volatile synchronized native strictfp interface
```
