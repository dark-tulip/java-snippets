# Random generators
## Static 
- not thread safe (если несколько потоков обращаются к одному и тому же статическому методу или переменной, они могут одновременно изменить состояние этого метода или переменной)
- потери на синхронизацию (приходится использовать доп синх блоки или локи)
  ```
  int n1 = Math.random() * 666;
  int n2 = Math.random() * 666;
  int n3 = Math.random() * 666;
  ```
## Antistatic
- thread safe (каждый поток имеет свой объект с которым работает)
- никаких потерь (нет лишних блоков синхронизации)
  ```
  Random random = new Random();
  int n1 = random.next() * 666;
  int n2 = random.next() * 666;
  int n3 = random.next() * 666;
  ```
# StringUtils static methods
### рекомендую если
- может вызывать кто угодно и когда угодно
- никогда не понадобится другая реализиация
- никогда не понадобиться кастомизация
- зависит только от параметром
### НЕ рекомендую если
- вы хотите ограничить его использование
- использует статическую переменную
- предпологаются изменения (или понадобится другая реализация)
- может захотеться насовать много if-ов посередине (спагетти код)

```java
static Person loadPerson(long id, boolean loadContacts) {
  Person p = fetchPerson(id);
  if (loadContacts) {
    p.contacts = fetchContacts(id);
  }
  return p;
}
```
- рано или поздно захочется вызвать на разных объектах

## можно ли переопределить статические методы
- фактически нет,
- практически да
- нельзя поставить аннотацию override, но создать метод с таким же названием в дочернем классе можно
```
public class Main2 {
  static void printHello() {
    System.out.println("Hello main2");
  }
}

public class Main extends Main2 {
  static void printHello() {
    System.out.println("Hello main1");
  }
  public static void main(String[] args) {
    printHello();
    Main2.printHello();
  }
}

// > Task :Main.main()
// Hello main1
// Hello main2
```

