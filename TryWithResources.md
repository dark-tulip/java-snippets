## Try with resources
- Try с ресурсами освобождает память после окончания, спасает от огромных массивов которые сделаны яерез java.io
- The resources will be closed after execution block
- Interface AutoCloseable (Closable) и переопределить метод close-
- Ресурс объявляется и инициализируется внутри try блока
- `try-with-resources` в качестве замены на `try-catch-finally`
- Объявление нескольких ресурсов (разделяя с (;))
- From Java 7
- From Java 9 can use `final` variables inside a try with resources block
- Final - значение переменной не изменяется после ее инициализации

```Java
public class ResourceOne implements AutoCloseable {
  @Override
  public void close() throws Exception {
    System.out.println("Second resource is closed");
  }
}
```
```Java
public class ResourceTwo implements AutoCloseable {
  @Override
  public void close() throws Exception {
    System.out.println("First resource is closed");
  }
}
```
```Java
final ResourceOne resourceOne = new ResourceOne();
final ResourceTwo resourceTwo = new ResourceTwo();

try (resourceTwo; resourceOne) {
  System.out.println("Printed something inside try-catch block");
}
```
```
// The output result
Printed something inside try-catch block
Second resource is closed
First resource is closed
```

### Использование и сравнение с константами
```Java
CONST.equals(someService.getMethod());
```
