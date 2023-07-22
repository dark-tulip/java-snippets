- статичные методы для работы с файлами
- interface Path and class Files
- `getParent()` - только для диекторий (возвращает родительские диретории)
- `resolve()` объединить первый Path со вторым
- `relativize()`

- резолв возвращает новый объект пути
```Java
    Path path2 = Paths.get("/Users").resolve("tansh");
    System.out.println(path2.resolve("Desktop").toAbsolutePath());  // /Users/tansh/Desktop
    System.out.println(path2.toAbsolutePath());                     // /Users/tansh
```

- `relativize()` - построить относительный путь (или как из первого получить второй)
- 
``` Java
   Path path2 = Paths.get("/Users").resolve("tansh");
    System.out.println(path2.resolve("Desktop").toAbsolutePath());  // /Users/tansh/Desktop
    System.out.println(path2.toAbsolutePath());                           // /Users/tansh

    System.out.println("dirPath: " + dirPath.toAbsolutePath());    // dirPath: /Users/tansh/Desktop/untitled
    System.out.println("path2: " + path2.toAbsolutePath());        // path2: /Users/tansh

    // как из dirPath получить path2
    System.out.println("dirPath.relativize(path2): " + dirPath.relativize(path2).toAbsolutePath());  // /Users/tansh/Desktop/untitled/../..

    // cd: no such file or directory: /Users/tansh/Desktop/untitled/Desktop/untitled
    System.out.println("path2.relativize(dirPath): " + path2.relativize(dirPath).toAbsolutePath());  //  /Users/tansh/Desktop/untitled/Desktop/untitled
```
