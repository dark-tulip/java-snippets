## Classical cron

scheduling a task to be executed at 10:15 AM on the 15th day of every month.
``` Java
@Scheduled(cron = "0 15 10 15 * ?", zone = "Europe/Paris")
```
By default, Spring will use the server's local time zone for the cron expression. However, we can use the zone attribute to change this timezone:

## In Spring
``` Java
@RestController
@RequestMapping("/test")
public class TestController {

  public String generateRndUser() {
    return new Faker().name().fullName();
  }

  List<String> usersRepo = new ArrayList<>();

  /**
   * Every 10 seconds
   */
  @Async  // to support parallel behavior in scheduled tasks
  @Scheduled(fixedDelay = 10_000)
  public void incrementUsers() {
    usersRepo.add(generateRndUser());  // generate rnd user
    System.out.println(" :: " + new Date() + " added new user: " + usersRepo);
  }

  @GetMapping
  public List<String> getUsers() {
    return usersRepo;
  }
}
```

<img width="500" alt="image" src="https://user-images.githubusercontent.com/89765480/195112567-4b0dc31f-93f9-4d30-8d88-28a0ff51acc8.png">


ACID
Normal forms
isolation levels


