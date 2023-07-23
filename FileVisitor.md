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

### SimpleFileVisitor - реализация обхода файлов по умолчанию
