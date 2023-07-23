### Files.walkFileTree(Path path, FileVisitor filevisitor)

- обход по дереву файлов
- путь начала прогулки
- нужно имплементировать интерфейс FileVisitor, который содержит четыре метода (до / после обхода папки, при нахождении файла и при ошибке файла)
- preVisitDirectory - как только зашли в директорию
- visitFile - при обращении к файлу
- postVisitDirectory - срабатывает после обращения ко всем элементам папки
- visitFileError - когда не удалось прочитать содержимое файла
- skip file siblings

### FileVisitResult - что делать дальше после каждого обхода
- CONTINUE - продолжить обход по дереву
- SKIP_SUBTREE - означает что в данную директорию заходить не нужно
- SKIP_SIBLINGS - продолжать обход по файлам не нужно
- TERMINATE - завершить обход
```Java

public class PathsAndFiles {
  public static void main(String[] args) throws IOException {
    Files.walkFileTree(Paths.get("new_folder"), new MyFileVisitor());
  }
}

class MyFileVisitor implements FileVisitor<Path> {
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    System.out.println("Зашли в директорию: " + dir.toAbsolutePath());
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    System.out.println("File found: " + file.getFileName());
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    System.out.println("File visitFileFailed: " + exc.getCause());
    return FileVisitResult.TERMINATE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    System.out.println("Exit from directory: " + dir.toAbsolutePath());
    return FileVisitResult.CONTINUE;
  }
}
```


### Способ копирования всех файлов в директории (SimpleFileVisitor - реализация обхода файлов по умолчанию)
- чтобы полность/ скопировать файл с содержимым можно использовать эту реализацию
- move тоже самое что и переименование?
```Java
public class FileCopier {
  public static void main(String[] args) throws IOException {
    Path path = Paths.get("new_folder2");
    Files.walkFileTree(path, new CopyFilesVisitor(path, Paths.get("total_copy")));
//    Files.move(Paths.get("asd4"), Paths.get("move"));  // move тоже самое что и переименование
  }
}

/**
 * Способ копирования всех файлов в директории
 */
class CopyFilesVisitor extends SimpleFileVisitor<Path> {

  Path source;
  Path target;

  public CopyFilesVisitor(Path source, Path target) {
    this.source = source;
    this.target = target;
  }

  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    System.out.println("preVisitDirectory: " + source.relativize(dir));                  // name
    System.out.println("preVisitDirectory: " + target.resolve(source.relativize(dir)));  // new name
    Path newDest = target.resolve(source.relativize(dir));             // new full dst
    Files.copy(dir,newDest, StandardCopyOption.REPLACE_EXISTING);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    throws IOException {
    System.out.println("visitFile: " + source.relativize(file));                  // name
    System.out.println("visitFile: " + target.resolve(source.relativize(file)));  // new name
    Path newDest = target.resolve(source.relativize(file));
    Files.copy(file, newDest, StandardCopyOption.REPLACE_EXISTING);
    return FileVisitResult.CONTINUE;
  }
}
```
