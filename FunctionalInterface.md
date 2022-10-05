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
