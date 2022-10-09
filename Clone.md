1. Неглубокое Клонирование
2. Глубокое Клонирование

#### Adding clone method to Book Model class
``` Java
    public Book clone () {

        Genre clonedGenre = new Genre(this.genre.getName(), this.genre.getDescription());
        Author clonedAuthor = new Author(this.author.getName());

        Book clonedBook = new Book();
        clonedBook.setId(this.id);
        clonedBook.setTitle(this.title);
        clonedBook.setStatus(this.status);
        clonedBook.setDescription(this.description);
        clonedBook.setGenre(clonedGenre);
        clonedBook.setAuthor(clonedAuthor);

        return clonedBook;
    }
```
Нужно перопределить eqauls and hash code для не примитивных атрибутов

``` Java
class BookTest {
  Book book;

  @BeforeEach
  public void declareBook() {
    book = new Book("bookName");
    book.setAuthor(new Author("authorName"));
    book.setTitle("bookTitle");
    book.setStatus(BookStatus.AVAILABLE);
    book.setDescription("this is short description of book");
    book.setId(123L);
    book.setGenre(new Genre("classic library", "description of genre"));
  }

  @Test
  void testClone() {

    Book clonedBook = book.clone();

    assertEquals(clonedBook.getAuthor(), book.getAuthor());
    assertEquals(clonedBook.getGenre(), book.getGenre());
    assertEquals(clonedBook, book);

    clonedBook.setStatus(BookStatus.NOT_AVAILABLE);

    assertEquals(book.getStatus(), BookStatus.AVAILABLE);
    assertEquals(clonedBook.getStatus(), BookStatus.NOT_AVAILABLE);
    assertNotEquals(clonedBook, book);
  }


  @Test
  void testClone__byClassField() {

    Book clonedBook = book.clone();

    clonedBook.getAuthor().setName("newAuthor");

    String authorFromBook = book.getAuthor().getName();
    String authorFromClonedBook = clonedBook.getAuthor().getName();

    System.out.println(authorFromBook);        // authorName
    System.out.println(authorFromClonedBook);  // newAuthor

    assertNotEquals(authorFromBook, authorFromClonedBook);

  }
}
```
