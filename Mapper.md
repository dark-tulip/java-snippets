// marks the interface as mapping interface

``` Java
// Create the interface 
@Mapper
public interface AuthorMapper {

  AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

  // expects the source object as parameter and returns the target object.
  @Mapping(target = "id", source = "id")
  @Mapping(target = "authorName", source = "name")
  AuthorDto authorToAuthorDto(Author author);
}
```

The tests 

```
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
