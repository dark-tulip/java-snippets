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
- fileChannel, ServerSocketChannel
- ByteBuffer, CharBuffer,
- Буфер это блок памяти, позволяющий читать и записывать данные
- FileChannel это АБСТРАКТНЫЙ КЛАСС, ()реализующий интерфейсы
-   SeekableByteChannel (позиция курсора в канале для чтения или установки позиции с помощью метода position(), ищущий)
-   GatheringByteChannel (write sequence of Bytebuffers, собирающий, `write(ByteBuffer[] arrs)`)
-   ScatteringByteChannel (read sequence of Bytebuffers за одну операцию, рассеивающий, `read(ByteBuffer[] arrs)`)

Режимы буфера
- read mode
- write mode
- capcacity
- position
- limit

  Выделить память в ByteBuffer можно двумя способами
  - allocate(int capacity) - буфер переданного размера, выделяемой в куче, управляет JVM
  - allocateDirect(int capacity) - off-heap память (прямая память, использует unsafe, НЕ управляется GC, медленный для малых объемов данных)
