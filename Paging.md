### Встроенная пагинация Spring Data JPA

#### Разница между Integer and int

Integer
- Integer - класс обертка, 
- if you want object to be able to be null
- Integer.parseInt("123"); // OK
- new Integer("123");      // OK
- Convert to another base: Integer.toBinaryString(123) // OK

int 
- примитивный тип данных 
- int cannot be null, may be zero if not initialized

### 1.Extend from PagingAndSortingRepository
```Java
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {
  @Override
  Page<Author> findAll(Pageable pageable);
}
```

### 2.FindAll using Pageable 
```Java
@Component
@RequiredArgsConstructor
public class AuthorService {

    public static final Integer ITEMS_PER_PAGE = 2;

    private final AuthorRepository authorRepository;

    public List<Author> getAuthorList(int pageNum, Integer itemsPerPage){

        itemsPerPage = itemsPerPage != null && itemsPerPage > 0
          ? itemsPerPage
          : ITEMS_PER_PAGE;

        Pageable pageable = PageRequest.of(pageNum, itemsPerPage);
        List<Author> authorList = authorRepository.findAll(pageable).toList();

        return author;
    }
}
```

### 3.Make controller with requested params
```Java
@RestController
@RequiredArgsConstructor
@RequestMapping(path = Constants.API_BASE + "authors")
public class AuthorController {
    @GetMapping
    public List<Author> getAuthorList(@RequestParam Integer pageNum, 
                                      @RequestParam(required = false) Integer itemsPerPage){
        return authorService.getAuthorList(pageNum, itemsPerPage);
    }
}
```
### Objecr relational mapping
#### Показать запрос который был отправлен в БД
``` 
spring.jpa.show-sql=true;
```
- прелесть JPA - object relational mapping - при различном наименовании атрибутов (столбцов) с моделькой Java
- Главная фишка  в маппинге полей с java объектом
``` Java
public interface StudentRepository extends JpaRepository<Student, Long> {
    // : ссылка на параметр 
    // s - ссылка на объект - entity

    @Query("SELECT s FROM students s WHERE (:deleted is null or s.deleted = :deleted) AND (:wantTypeId is null or s.wantTypeId = :wantTypeId)")
    List<Student> findAllFiltered(Boolean deleted, Long wantTypeId);

    // прямой запрос в БД, nativeQuery
    @Query(value = "SELECT * FROM students WHERE is_deleted = :deleted", nativeQuery=true);
    List<Student> findByQustomQuery(Boolean deleted, Long wantType)
}
```
