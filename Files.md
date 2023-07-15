## Работа с файлами на Java

*Streams* (в рамках работы с файлами) - упорядоченная последовательность данных

Типы файлов можно разделить на два типа:
- бинарные файлы (запись потоком байтов)
- текстовые файлы (запись потоком символов)
при работе с файлами, в Java нужно использовать разные типы стримов

- InputStream это абстрактный класс
- FileReader это тоже абстрактный класс

*Расстановка путей до файлов в Java*
- абсолютный путь (относительно корня ОС)
- относительный путь (относительно корневой директории проекта)
<hr>

### FileWriter and FileReader
- используется для записи потока символов (**Character Input Stream**)
- !! USE try with resources чтобы закрыть поток
- флаг append дописывает в файл, НЕ перезаписывает
- FileReader and FileWriter ДЛЯ РАБОТЫ С ТЕКСТОВЫМИ файлами

### BufferedReaer and BufferedWriter
- добавляют функциональность буфферизации
- обертки для FileReader and FileWriter
- используйте `BufferedReader()`, `BufferedWriter()` для чтения и записи данных порциями, по умолчанию в 8192 символов (Большинство ОС имеют такой размер буфера* https://bugs.openjdk.org/browse/JDK-4953311)
- `implements AutoCloseable` чтобы ресурс можно было использовать в tryWithResources

пример буфферизации:
процесс загрузки части видео и его воспроизведения, пока остальная часть будет загружаться

### FileInputStream and FileOutputStream
- для работы с бинарными файлами
- НЕЛЬЗЯ использовать стримы закодированных символов для работы с бинарными файлами

``` Java
 try (
      // использование символьных буфферов приведет к потере данных при работе с байтовыми форматами фалов
      BufferedReader br = new BufferedReader(new FileReader("img.png"));
      BufferedWriter bw = new BufferedWriter(new FileWriter("img_output.png", false))
    ) {
      int ch;
      while ((ch = br.read()) != -1) {
        bw.write(ch);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
```
<div align="center">
 
|<img width="600" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/79a0ccb8-e99d-441e-8a6c-b839e81e7359"> | 
|:--:| 
| *Пример копирования с сharacter streams  - ломает бинарный файл изображения* |
</div>

<hr>

```Java
try (
      FileInputStream fis = new FileInputStream("img.png");
      FileOutputStream fout = new FileOutputStream("img_output_fout.png", false)
    ) {
      int ch;
      while ((ch = fis.read()) != -1) {
        fout.write(ch);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
```

<div align="center">

| <img width="600" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/f57dcd2f-3fad-44e5-a94c-19dcacc6a6ad"> |
|:--:| 
| *Using streams of RAW BYTES* |

</div>

<hr>

Для ускорения чтения потока байтов можно их буфферизировать, результат такой же, это обертка над FileInputStream

``` Java
import java.io.*;

public class MainEx {
  public static void main(String[] args) {
    try (
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream("img.png"));
      BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream("img_bout.png", false))
    ) {
      int ch;
      while ((ch = bis.read()) != -1) {
        bout.write(ch);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```

<div align="center">

| <img width="1034" alt="image" src="https://github.com/dark-tulip/course-java/assets/89765480/d9c94a52-f708-4933-a900-72e28032ac51"> |
|:--:| 
| *Using streams of RAW BYTES* |

</div>

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

