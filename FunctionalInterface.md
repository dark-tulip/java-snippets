## Реализация функционального интерфейса на java

- Функциональные интерфейсы нужны для создания анонимных классов с одним абстрактным методом
- From Java 8
- @FunctionalInterface - сообщить компилятору что может быть только один метод (просто доп страховка)
- Вместе достпуны статические и дефолтные методы
- Только один метод чтобы реализовать лямбду
- foreach, аналогичен методу peek, разница в том, что он конечный — терминальный.

``` Java
<T> – the type of the input to the function 
<R> – the type of the result of the function
<E> – the type of elements held in this deque
<K> – the type of keys maintained by this map 
<V> – the type of mapped values
<X> – no value is present
<N> - node we are pointing to
```

``` Java

@FunctionalInterface
public interface Converter<T, N> {

  N convert(T t);
  
  static void printMsg() {
    System.out.println("HELLO, I'm static void method");
  }
  
}

/* Using cases */
public class Main {
    public static void main(String[] args) {
        Converter<String, String> myConverter = new Converter<String, String>() {
            @Override
            public String convert(String s) {
                return s.toUpperCase().substring(0, 2);  // Получить первые два символа в верхнем регистре
            }
        };
        System.out.println(myConverter.convert("testststststs"));  // TE
        System.out.println(myConverter.convert("TTTTTSTSFFS"));    // TT
        System.out.println(myConverter.convert("LUFGGFLS"));       // LU
        System.out.println(myConverter.convert("23462387"));       // 23


        Converter<String, Integer> fromStrToInt = Integer::parseInt;
        System.out.println(fromStrToInt.convert("123"));        // 123
        System.out.println(fromStrToInt.convert("353453"));     // 353453
        System.out.println(fromStrToInt.convert("44444"));      // 44444
        System.out.println(fromStrToInt.convert("23462387"));   // 23462387


        Converter<Integer, String> fromIntToStr2 = num -> "Int Converted to string " + (num + 111);
        System.out.println(fromIntToStr2.convert(1231));      // Int Converted to string 1342
        System.out.println(fromIntToStr2.convert(2376423));   // Int Converted to string 2376534
        System.out.println(fromIntToStr2.convert(7));         // Int Converted to string 118


        Converter<String, Integer> fromIntToStr3 = s -> {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return 0;
            }
        };
        System.out.println(fromIntToStr3.convert("1111"));             // 1111
        System.out.println(fromIntToStr3.convert("error"));            // 0
        System.out.println(fromIntToStr3.convert("will print zero"));  // 0
        System.out.println(fromIntToStr3.convert("63.99"));            // 0

        Converter.printMsg();  // HELLO, I'm static void method
    }
}
```

### Scope visibility
- inner class and lambda expression difference in their scope by `this` keywork

```Java
public class TestScope {
  
  @FunctionalInterface
  interface IMovable {
    void move();
  }
  
  String animal = "GLOBAL_ANIMAL";
  
  void testScopes() {
    IMovable iMovable1 = new IMovable() {
      String animal = "iMovable1";
      public void move() {
        System.out.println("anonymous class: " + this.animal);  // iMovable1
      }
    };

    IMovable iMovable2 = () -> {
      String animal = "iMovable2";
      System.out.println("functional interface: "  + this.animal);  // GLOBAL_ANIMAL
      System.out.println("functional interface: "  + animal);       // iMovable2
    };

    iMovable1.move();
    iMovable2.move();
  }

  public static void main(String[] args) {
   new TestScope().testScopes();
  }
}
```
## Effectively final
- effectively final переменные - которые не объявлены как final и
- **после инициализации не меняют свое значение**
- to avoid concurrency issues use only final or effectively final variables
- only for anonymous classes and lambda expressions
```Java
    int[] arr = new int[]{1, 1, 1};
    int a = 10;
    
    Runnable runnable = () -> {
      arr[0] += a; // allowed
      // a++;      // NOT allowed
    };
    runnable.run();
    
    // a++;  // NOT allowed
    
    System.out.println(arr[0]);  // 11
```
- оптимизатор НЕ отпимизирует effectivel final переменные, только те, что явно объявлены как final


##

```java
    public static void main(String[] args) {
        Predicate<Integer>        predicate      = x -> x > 0;
        Consumer<Integer>         consumer       = System.out::println;
        Function<Integer, String> function       = x -> x + "asd";
        Supplier<Integer>         supplier       = () -> 10;
        UnaryOperator<Integer>    unaryOperator  = x -> x + x;
        BinaryOperator<Integer>   binaryOperator = (x, y) -> x * y;

        consumer.accept(
                binaryOperator
                        .andThen(unaryOperator)  // 12 + 12 =24
                        .andThen(unaryOperator)  // 6 + 6 = 12
                        .apply(2, 3)       // 6
        ); // 24

        System.out.println(
                function
                        .andThen(x -> x + "QWE")
                        .apply(supplier.get())
        );  // 10asdQWE

        consumer.andThen(consumer)
                .andThen(consumer)
                .accept(supplier.get());  // 10 // 10  // 10

        System.out.println(predicate.test(10));  // true

    }

```
