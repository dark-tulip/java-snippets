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

