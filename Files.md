Streams (в рамках работы с файлами) - упорядоченная последовательность данных

Типы файлов можно разделить на два типа:
- бинарные файлы (запись потоком байтов)
- текстовые файлы (запись потоком символов)
при работе с файлами, в Java нужно использовать разные типы стримов
<hr>
### Расстановка путей до файлов в Java
- абсолютный путь (относительно корня ОС)
- относительный путь (относительно корневой директории проекта)
<hr>

### FileWriter and FileReader
- используется для записи потока символов (character-input stream)
- !! USE try with resources чтобы закрыть поток
- флаг append дописывает в файл, НЕ перезаписывает
- FileReader and FileWriter ДЛЯ РАБОТЫ С ТЕКСТОВЫМИ файлами
- используйте BufferedReader(), BufferedWriter() для чтения и записи данных порциями, по умолчанию в 8192 символов (Большинство ОС имеют такой размер буфера* https://bugs.openjdk.org/browse/JDK-4953311) 


#### Yandex Contest Java Input example
- Java шаблон для контестов
```Java
import java.io.*;
public class Main {
  public static void main(String[] args) {
    try (
      BufferedReader br = new BufferedReader(new FileReader("input.txt"));
      BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))
    ) {
      int n = Integer.parseInt(br.readLine());

      bw.append("" + n);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```

