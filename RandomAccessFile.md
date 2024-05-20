### RandomAccessFiles
- позволяет читать информацию из любого места в файле и записывать эту информацию в любое место в файле
- работает с файлом установив для него курсор
- метод `seek()` - устанавливает курсор на место записи или чтения
- `getFilePointer()` - получить позицию указателя
- `writeBytes()` - перезаписывает с момента установленного курсора
- FileDescriptor описывает режимы работы с файлом (нативные `stdin/stdout/stderr`)

Access modes - способы открытия канала для работы с файлом
* `r` - кидает Exception при попытке записать из методов объекта RandomAccessFile
* `rw`, `rwd` - перезаписывают метаданные о времени записи ТОЛЬКО после закрытия ресурса
* `rws` медленней, но обновляет информацию каждый раз при записи контента

```Java
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileEx {
  public static void main(String[] args) {
    try (RandomAccessFile raf = new RandomAccessFile("input.txt", "rwd");) {
      raf.write('n');
      raf.write('\n');
      raf.write('n');

      System.out.println("FILE LENGTH: " + raf.length());  // длина файла (кол-во символов в байтах)
      System.out.println("getFilePointer: " + raf.getFilePointer());  // указатель курсора находится

      System.out.println(raf.readLine());
      raf.writeBytes("raf.write('n');");
      System.out.println(raf.readLine());

      raf.seek(0);  // установить пойнтер (или курсор на начало файла)
      raf.writeBytes("-----");  // перезапишет с места начала курсора

      System.out.println(raf.readLine());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```
