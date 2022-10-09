Mapper annotation marks the interface as mapping interface


### The Author model
``` Java
@Entity
public class Author extends BaseEntity {

    private String name;

    public Author (String name) {
        this.setName(name);
    }

    public Author (Long id, String name) {
        this.setId(id);
        this.setName(name);
    }

    public Author() { }
}
```

#### Declaring the authorDto object
``` Java
@Value
@Builder
public class AuthorDto {
    private long id;
    private String authorName;
}
```
#### Create the  mapping interface 
``` Java
@Mapper
public interface AuthorMapper {

  AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

  // expects the source object as parameter and returns the target object.
  @Mapping(target = "id", source = "id")
  @Mapping(target = "authorName", source = "name")
  AuthorDto authorToAuthorDto(Author author);
}
```
#### The tests 
``` Java
    @Test
    void shouldMapAuthorToDto() {
        // given
        Long id = 111L;
        String name = "authorName";
        Author author = new Author(id, name);

        // when
        AuthorDto authorDto = AuthorMapper.INSTANCE.authorToAuthorDto(author);

        // then
        assertNotNull(author);
        assertEquals(authorDto.getId(), id);
        assertEquals(authorDto.getAuthorName(), name);
    }

    @Test
    void shouldMapAuthorToDto__onlyName() {
        // given
        String name = "authorName";
        Author author = new Author(name);

        // when
        AuthorDto authorDto = AuthorMapper.INSTANCE.authorToAuthorDto(author);

        // then
        assertNotNull(author);
        assertEquals(authorDto.getAuthorName(), name);
        assertEquals(authorDto.getId(), 0);
    }
    
```
### Mapping for Book model
``` Java
@Mapper
public interface BookMapper {

  BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "author.name", target = "author")
  @Mapping(source = "genre.name", target = "genre")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "status", target = "status")
  BookDto bookToDto(Book book);
}
```
#### The tests
``` Java
class BookMapperTest {

  @Test
  void shouldMapBookToBookDto() {
    Book book = new Book("book");

    book.setAuthor(new Author("authorName"));
    book.setTitle("bookTitle");
    book.setStatus(BookStatus.AVAILABLE);
    book.setDescription("this is short description of book");
    book.setId(123L);
    book.setGenre(new Genre("classic library", "description of genre"));

    BookDto bookDto = BookMapper.INSTANCE.bookToDto(book);

    assertEquals(bookDto.getId(), book.getId());
    assertEquals(bookDto.getTitle(), book.getTitle());
    assertEquals(bookDto.getGenre(), book.getGenre().getName());
    assertEquals(bookDto.getAuthor(), book.getAuthor().getName());
    assertEquals(bookDto.getStatus(), book.getStatus());
    assertEquals(bookDto.getDescription(), book.getDescription());
  }

  @Test
  void shouldMapBookToBookDto__onlyName() {
    Book book = new Book("bookName");

    BookDto bookDto = BookMapper.INSTANCE.bookToDto(book);

    assertEquals(bookDto.getTitle(), book.getTitle());

    System.out.println(bookDto);  // BookDto(id=0, title=book, author=null, description=null, genre=null, status=null)
    System.out.println(book);     // Book(id=null, title=book, status=null, genre=null, author=null, description=null)
  }
}
```
