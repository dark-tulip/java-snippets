- интерфейсы можно расширять

```Java
interface Test extends AutoCloseable, Closeable {
  ...
}
```
# Channel and buffer
- Channel как стрим, но в отличие от стрима он двухсторонний
- channels capable to perform distinct I/O operations
- Read:  **File -> Channel -> Buffer -> Program**
- Write: **Program -> Buffer -> Channel -> File**
- Channel всегда работает вместе с буфером
- Буфер это блок памяти, позволяющий читать и записывать данные
- FileChannel это АБСТРАКТНЫЙ КЛАСС, ()реализующий интерфейсы
-   SeekableByteChannel (позиция курсора в канале для чтения или установки позиции с помощью метода position(), ищущий)
-   GatheringByteChannel (write sequence of Bytebuffers, собирающий, `write(ByteBuffer[] arrs)`)
-   ScatteringByteChannel (read sequence of Bytebuffers за одну операцию, рассеивающий, `read(ByteBuffer[] arrs)`)

Режимы буфера
- read mode
- write mode
- capcacity (размер буфера)
- position (при инит равна нулю)
- limit () - кол-во байтов которые мы можем прочитать (`при флипе лимит равен его послледней записанной позиции`)
- `clear()` вернуть позицию к нулю; а лимит к размеру capacity, благодаря чему старые данные перезаписываются
- `flip()` -> (лимит заменяет на текущую позицию, ЗАТЕМ позицию на ноль)
- `channel.read(byffer)` - прочитать инфо из файла и записать в буфер; также сместить курсор в файле; return -1 if EOF
- благодаря флипу мы понимаем что дальше уже читать не нужно
```Java
try (RandomAccessFile file = new RandomAccessFile("input.txt", "rw");
         FileChannel channel = file.getChannel();
    ) {
      print("channel.size(): " + channel.size());
      print("channel.position(): " + channel.position());

      ByteBuffer buffer = ByteBuffer.allocate(20);
      print("1) pos: " + buffer.position() + " limit: " + buffer.limit());  // 1) pos: 0 limit: 20
      channel.read(buffer);  // прочитать и заполнить буфер реальными данными
      print("2) pos: " + buffer.position() + " limit: " + buffer.limit());  // 2) pos: 20 limit: 20
      buffer.flip();  // перевести в режим чтения, лимит на позицию, позицию на ноль
      print("3) pos: " + buffer.position() + " limit: " + buffer.limit());  // 3) pos: 0 limit: 20
      buffer.getInt();  // прочитать четыре байта; поз+4, limit same
      print("4) pos: " + buffer.position() + " limit: " + buffer.limit());  // 1) pos: 4 limit: 20
      buffer.flip();  // limit = 4, pos = 0
      print("5) pos: " + buffer.position() + " limit: " + buffer.limit());  // 1) pos: 0 limit: 4

    } catch (IOException e) {
      e.printStackTrace();
}
```
- после чтения данных, позииция сокращается; читает только до лимита
- после клеар и позиция и лимит проходят в исходное состояние
``` Java
      buffer.getInt();
      print("8) pos: " + buffer.position() + " limit: " + buffer.limit());  // 8) pos: 4 limit: 4

      buffer.clear();
      print("9) pos: " + buffer.position() + " limit: " + buffer.limit());  // 9) pos: 0 limit: 20
```
  Выделить память в ByteBuffer можно двумя способами
  - allocate(int capacity) - буфер переданного размера, выделяемой в куче, управляет JVM
  - allocateDirect(int capacity) - off-heap память (прямая память, использует unsafe, НЕ управляется GC, медленный для малых объемов данных)

```Java
    try (RandomAccessFile file = new RandomAccessFile("input.txt", "rw");
         FileChannel channel = file.getChannel();) {
      print("channel.size(): " + channel.size());
      print("channel.position(): " + channel.position());

      ByteBuffer buffer = ByteBuffer.allocate(3);

      int readBytes = channel.read(buffer);  // прочитать из канала и записать в буфер

      System.out.println("readBytes: " + readBytes);
      System.out.println(buffer.position());
      System.out.println(buffer.limit());
      System.out.println(buffer.capacity());
      buffer.flip();
      
      System.out.println("ch: " + (char) buffer.get());
      System.out.println(buffer.position());
      System.out.println(buffer.limit());
      System.out.println(buffer.capacity());
      buffer.clear();
      
//      channel.size(): 127
//      channel.position(): 0
//      readBytes: 3
//      3
//      3
//      3
//      ch: T
//      1   - два байта уже прочитали, теперь можно прочитать один
//      3
//      3
```
### Чтение из файла используя канал
```Java
StringBuilder str = new StringBuilder();

    try (RandomAccessFile file = new RandomAccessFile("input.txt", "rw");
         FileChannel channel = file.getChannel();) {
      print("channel.size(): " + channel.size());
      print("channel.position(): " + channel.position());

      ByteBuffer buffer = ByteBuffer.allocate(77);

      int readBytes = channel.read(buffer);  // прочитать из канала и записать в буфер

      while (readBytes != -1) {
        System.out.println("read: " + readBytes);
        buffer.flip();

        while (buffer.hasRemaining()) {
          str.append((char) buffer.get());
        }

        buffer.clear();
        readBytes = channel.read(buffer);
      }

      System.out.println(str);
    }
```
