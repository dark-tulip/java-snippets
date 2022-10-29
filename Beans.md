1. Create BookService interface
2. Create BookServiceImpl service implementing interface (@Service annotattion)
3. Create BookServiceStubbedImpl service implementing interface (@Service annotattion)

4. Вызвать нужный бин с нового сервиса.
5. Сделать дефолтным BookServiceImpl как дефолтную реализацию BookService.


### Как подруджить lombok @RequiredArgsConstructor с @Qualifier
```Java
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  @Qualifier("bookServiceStubbedImpl")
  private final BookService bookService;
  }
  
  @GetMapping("/service-type")
  public String getBookImpl() {
    return bookService.getServiceType();
  }
}
```

```
// lombok.config file on the application root 
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier
```

#### Qualifier
Квалификатор аннотации нужен чтобы конкретно указать спрингу какой нам нужен бин
Когда есть несколько бинов реализующих интерфейс

```Java
public interface BookService {
  String getServiceType();
}

@Service
@AllArgsConstructor
public class BookServiceStubbedImpl implements BookService {

  private final BookRepository bookRepository;

  public String getServiceType(){
    return "This is Faked book service";
  }
}

@Primary
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;

  public String getServiceType(){
    return "This is real book service";
  }
}
```
