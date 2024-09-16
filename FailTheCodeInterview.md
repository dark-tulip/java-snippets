### Огромный список вопросов для подготовки к интевью

https://github.com/enhorse/java-interview/blob/master/README.md

### 1. Final/Finally/Finalize

### 2. Что вернет `try/catch`?

```java
    public static void main(String[] args) {
        System.out.println(foo());
    }

    static int foo() {
        try {
            return 10;
        } finally {
            return 20;
        }
    }
```
### 3. Что вернет?
```java
System.out.println(10/3);
```

### 4. Что вернет `try/catch`?
```java
    public static void main(String[] args) {
        System.out.println(foo());
    }

    static int foo() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e ) {
            throw new NullPointerException();
        } finally {
            return 20;
        }
    }
```

### 5. Swap the map, why?

```java
    public static void main(String[] args) {
        HashMap<String, String> numbers = new HashMap<String, String>() {{
            put("a", "1111");
            put("b", "2222");
        }};

        HashMap<String, String> strings = new HashMap<String, String>() {{
            put("a", "aaaa");
            put("b", "bbbb");
        }};

        numbers.replaceAll(strings::put);
        System.out.println(numbers);
        System.out.println(strings);
    }
```
