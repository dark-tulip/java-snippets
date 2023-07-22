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
#### Files class

```Java
    Path file1 = Paths.get("input.sh");
    Path file2 = Paths.get("input3.txt");
    Path dir = Paths.get("new_folder");

    if (!Files.exists(dir)) {
      System.out.println("Dir not found: " + dir);
      Files.createDirectory(dir);
    }
    if (!Files.exists(file1)) {
      Files.createFile(file1);
      System.out.println("created file1: " + file1);
    }

    if (!Files.exists(file2)) {
      Files.createFile(file2);
      System.out.println("created file2: " + file2);
    }

    System.out.println("isSameFile: " + Files.isSameFile(file1, file2));  // isSameFile: false
    System.out.println("isHidden: " + Files.isHidden(file1));             // isHidden: false
    System.out.println("isReadable: " + Files.isReadable(file1));         // isReadable: true
    System.out.println("isWritable: " + Files.isWritable(file1));         // isWritable: true
    System.out.println("isExecutable: " + Files.isExecutable(file1));     // isExecutable: false
    System.out.println("size: " + Files.size(file1));                     // size: 11
    System.out.println("getOwner: " + Files.getOwner(file1));             // getOwner: tansh
    
    // getAttribute: {lastAccessTime=2023-07-22T17:21:41.656819502Z, lastModifiedTime=2023-07-22T17:21:40.444193826Z, size=11, creationTime=2023-07-22T17:21:17Z, isSymbolicLink=false, isRegularFile=true, fileKey=(dev=1000010,ino=47751573), isOther=false, isDirectory=false}
    System.out.println("getAttribute: " + Files.readAttributes(file1, "*"));             // read set of attribs

    // getAttribute: 2023-07-22T17:21:41.656819502Z
    System.out.println("getAttribute: " + Files.getAttribute(file1, "lastAccessTime"));   // read the value of attrib
```

#### Move, Copy and Delete
- скопировать файл можно в директорию указав имя файла
- при повторном запуске файл не перезаписывается, и сохраняет старую копию (or else java.nio.file.FileAlreadyExistsException)
- чтобы перезаписать - используйте опцию `StandardCopyOption.REPLACE_EXISTING`
```Java
Files.copy(file1, dir.resolve("copied"), StandardCopyOption.REPLACE_EXISTING); 
```
- НЕЛЬЗЯ КОПИРОВАТЬ ПАПКУ С ФАЙЛАМИ, ПАПКА СКОПИРУЕТСЯ, СОДЕРЖИМОЕ - НЕТ!
- **Переименовать файл** - перемещает внутренние директории и файлы
```Java
Files.move(Paths.get("new_folder"), Paths.get("new_folder2"));
```
- Нельзя удалить папку если внутри нее есть файлы
```Java
Files.deleteIfExists(Paths.get("src").resolve("test"));
```

### File.readAllLines()

```Java
    Path file1 = Paths.get("in.txt");
    if(!Files.exists(file1)) {
      Files.createFile(file1);
    }
    String lines = "row1\nrow2\nrow3";
    Files.write(file1, lines.getBytes());
    List<String> fileLines = Files.readAllLines(file1);  // [row1, row2, row3]
    // row1
    // row2
    // row3
    for(var line : fileLines) {
      System.out.println(line);
    }
  }
```
