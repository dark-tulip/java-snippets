### RandomAccessFiles
- работает с файлом установив для него курсор
- метод `seek()` - устанавливает курсор на место записи или чтения
- `writeBytes()` - перезаписывает с момента установленного курсора
- FileDescriptor описывает режимы работы с файлом (нативные `stdin/stdout/stderr`)

Access modes - способы открытия канала для работы с файлом
* `r` - кидает Exception при попытке записать из методов объекта RandomAccessFile
* `rw`, `rwd` - перезаписывают метаданные о времени записи ТОЛЬКО после закрытия ресурса
* `rws` медленней, но обновляет информацию каждый раз при записи контента

```Java
public class RandomAccessFileEx {
  public static void main(String[] args) {
    try (RandomAccessFile raf = new RandomAccessFile("input.txt", "rw");) {
      raf.write('n');
      raf.write('\n');
      raf.write('n');

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
/*
Output:
-----f gjgh
raf.write('n');('n')dfsdfs
sdf
*/
```
