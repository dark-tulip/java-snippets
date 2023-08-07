- метки используемые для передачи определенной информации
## Target (цель) - описывает область применения аннотации
- `TYPE` (class, enum, interface)
- `FIELD` 
- `METHOD`
- `CONSTRUCTOR`
- `PARAMETER` - параметры метода или класса

## Retention - жизненный цикл аннотации
- `SOURCE` - есть в источнике кода, нет в байт коде (отбрасывается компилятором)
- `CLASS`- есть в байт коде, но нет в рантайме (отбрасывается JVM) - default
- `RUNTIME` - работает в рантайме
```Java

// create annotation
// set methods
// set default
// get class - get annotations
// for reflection USE RUNTIME, CLASS WILL NOT WORK


@OperatingSystem(year = 2222, terminalType = TerminalType.CMD)
class Windows {
}

@OperatingSystem(displayName = "Linux Ubuntu 22.04", terminalType = TerminalType.BASH, year = 1991)
class Linux {
}

public class JavaAnnotations {

    public static void main(String[] args) throws ClassNotFoundException {

        Class windows = Class.forName("org.example.Windows");
        Class ubuntu = Class.forName("org.example.Linux");

        OperatingSystem windowsAnnotation = (OperatingSystem) windows.getAnnotation(OperatingSystem.class);
        System.out.printf("displayName: %s, terminalType: %s, year: %s%n", windowsAnnotation.displayName(), windowsAnnotation.terminalType(), windowsAnnotation.year());

        OperatingSystem ubuntuAnnotation = (OperatingSystem) ubuntu.getAnnotation(OperatingSystem.class);
        System.out.printf("displayName: %s, terminalType: %s, year: %s%n", ubuntuAnnotation.displayName(), ubuntuAnnotation.terminalType(), ubuntuAnnotation.year());

        /*
        displayName: Windows, terminalType: CMD, year: 2222
        displayName: Linux Ubuntu 22.04, terminalType: BASH, year: 1991
        */

        System.out.println("toString:                       " + ubuntuAnnotation);  // @org.example.OperatingSystem(year=1991, displayName="Linux Ubuntu 22.04", terminalType=BASH)
        System.out.println("annotationType:                 " + ubuntuAnnotation.annotationType());
        System.out.println("annotationType.getSimpleName:   " + ubuntuAnnotation.annotationType().getSimpleName());
        System.out.println("getClassLoader.getClassLoader:  " + ubuntuAnnotation.annotationType().getClassLoader());
        System.out.println("getAnnotations:                 " + Arrays.toString(ubuntuAnnotation.annotationType().getAnnotations()));

        /**
         * toString:                       @org.example.OperatingSystem(year=1991, displayName="Linux Ubuntu 22.04", terminalType=BASH)
         * annotationType:                 interface org.example.OperatingSystem
         * annotationType.getSimpleName:   OperatingSystem
         * getClassLoader.getClassLoader:  jdk.internal.loader.ClassLoaders$AppClassLoader@5cb0d902
         * getAnnotations:                 [@java.lang.annotation.Target({TYPE}), @java.lang.annotation.Retention(RUNTIME)]
         */
    }
}

enum TerminalType {
    TERMINAL,
    BASH,
    CMD
}


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface OperatingSystem {
    String displayName() default "Windows";

    TerminalType terminalType();

    int year() default 2001;

}
```
- если установить для аннотаций RETENTION CLASS - сломаются во время рантайма - `Cannot invoke "org.example.OperatingSystem.displayName()" because "windowsAnnotation" is null`
- @interface
- внутренние поля прописаны как методы
- default - должно принимать constant значение по умолчанию
