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
