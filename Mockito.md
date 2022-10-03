# Testing service with mockito

`verify` - используется для верификации вызовов (наличие вызова какого либо метода, в основном void)<br>
`capture` - захватчик, работает на подобие прокси<br>
`stub` - заглушка<br>
`mock` -  это фиктивные обхекты, внедрить объекты для переменной экземпляра<br>
Жесткий порядок вызова методов можно задать с помощью специального объекта InOrder (затем с verify)


``` Java
package com.ngariful.author.service;

import com.ngariful.author.dto.AuthorDto;
import com.ngariful.author.model.Author;
import com.ngariful.author.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    AuthorService authorService;
    @Mock
    AuthorRepository authorRepository;

    @Captor
    ArgumentCaptor<Author> authorCaptor;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void getAuthorList() {

        // given
        Author author1 = new Author(1L, "test1");
        Author author2 = new Author(2L, "test2");

        List<Author> authorList = Arrays.asList(author1, author2);

        List<AuthorDto> authorDtoList = authorList.stream()
                .map(authorService::convertToDto)
                .collect(Collectors.toList());

        // when
        when(authorRepository.findAll()).thenReturn(authorList);

        // then
        assertEquals(authorDtoList, authorService.getAuthorList());

    }


    @Test
    void getAuthor() {
        //given
        long id = 1L;
        Author expectedAuthor = new Author("test");

        //when
        when(authorRepository.findById(id)).thenReturn(Optional.of(expectedAuthor));

        //then
        Author actualAuthor = authorService.getAuthor(id);
        assertEquals(expectedAuthor, actualAuthor);
    }

    @Test
    void updateAuthor() {
        // given
        long id = 1L;
        String name = "test";
        String newName = "newName";

        Author author = new Author(id, name);

        // when
        when(authorRepository.findById(id)).thenReturn(Optional.of(author));
        author.setName(newName);
        authorService.updateAuthor(author);
        Author updatedAuthor = authorService.getAuthor(id);

        // Using verify and mockito
        verify(authorRepository, times(1)).save(authorCaptor.capture());
        verify(authorRepository, atLeastOnce()).findById(id);
        verify(authorRepository, never()).findAll();

        // then
        assertEquals(updatedAuthor, authorCaptor.getValue());
    }


    @Test
    void testGetAuthorsList() {
        // проверка что findAll был вызван (сервис getAuthorList содержит вызов метода findAll из репозитория)
        authorService.getAuthorList();
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testGetAuthorUpdate() {
        authorService.updateAuthor(any());
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    void testGetAuthor() {
        long id = 1L;
        String name = "test";

        Author author = new Author(id, name);
        authorRepository.save(author);

        // проверка что при вызове сервиса из репозитория был вызван метод findById 1 раз
        authorService.getAuthor(id);
        verify(authorRepository, times(1)).findById(id);
    }

}
```
code samples and snippets from java course
