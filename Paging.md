### Встроенная пагинация Spring Data JPA

### Extend from PagingAndSortingRepository
```Java
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {
  @Override
  Page<Author> findAll(Pageable pageable);
}
```

### FindAll using Pageable 
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

### Make controller with requested params
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

