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
