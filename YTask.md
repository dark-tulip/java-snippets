### models

```java
package ru.anvera.models;

import java.util.List;

public class CustomerBasket {
  private Long id;
  public Long getUserId() {
    return userId;
  }

  private Long           userId;
  private List<Purchase> purchases;

  public CustomerBasket(Long id, Long userId, List<Purchase> purchases) {
    this.id        = id;
    this.userId    = userId;
    this.purchases = purchases;
  }

  public List<Purchase> getPurchases() {
    return purchases;
  }
}


package ru.anvera.models;
public class Purchase {
  private Long   id;
  private String name;

  public Integer getCost() {
    return cost;
  }

  private Integer cost;

  public void setCostAfterDiscount(Integer costAfterDiscount) {
    this.costAfterDiscount = costAfterDiscount;
  }

  public Integer getCostAfterDiscount() {
    return costAfterDiscount;
  }

  private Integer costAfterDiscount;

  public Purchase(Long id, String name, Integer cost, Integer costAfterDiscount) {
    this.id                = id;
    this.name              = name;
    this.cost              = cost;
    this.costAfterDiscount = costAfterDiscount;
  }
}


package ru.anvera.models;
public class User {
  private Long    id;
  private Integer discount;

  public User(Long id, Integer discount) {
    this.id       = id;
    this.discount = discount;
  }
}

```

### services

```java
package ru.anvera.service;

import java.util.concurrent.CompletableFuture;

public interface AsyncDiscountServiceCaller {
  CompletableFuture<Integer> getDiscount(Long userId);
}

```

```java
package ru.anvera.service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class AsyncDiscountServiceCallerImpl implements AsyncDiscountServiceCaller {
  @Override
  public CompletableFuture<Integer> getDiscount(Long userId) {

    HashMap<Long, Integer> userIdAnnDiscountAmountMap = new HashMap<>();
    userIdAnnDiscountAmountMap.put(1L, 10);
    userIdAnnDiscountAmountMap.put(2L, 10);
    userIdAnnDiscountAmountMap.put(3L, 1);
    CompletableFuture<Integer> test = new CompletableFuture<>();

    test.complete(userIdAnnDiscountAmountMap.getOrDefault(userId, 0));
    return test;
  }
}
```

```java
package ru.anvera.service;


import ru.anvera.models.CustomerBasket;


public class DiscountService {
  private final AsyncDiscountServiceCaller asyncDiscountServiceCaller;

  public DiscountService(AsyncDiscountServiceCaller asyncDiscountServiceCaller) {
    this.asyncDiscountServiceCaller = asyncDiscountServiceCaller;
  }

  /**
   * на вход получает id покупателя и корзину
   * вычисляет и применяет скидки
   * возвращает корзину, в которой учтены скидки. Скидка учитывается в стоимости покупки
   *
   * @param customerBasket с учетом скидки
   */
  public void applyDiscount(CustomerBasket customerBasket) {
    /*
     *
     * Настройки скидок лежат во внешнем сервисе (БД, HTTP и т.д.).
     *
     * Доступ к сервису асинхронный.
     *
     * Нужно:
     * - разработать асинхронный интерфейс сервиса, хранящего настройки
     * - использовать его при расчёте скидок
     * - адаптировать тесты и написать новые, если необходимо
     *
     */
    asyncDiscountServiceCaller.getDiscount(customerBasket.getUserId()).thenApply(discount -> {
          if (discount > 100 || discount < 0) {
            throw new IllegalArgumentException("wrong amount of discount: " + discount);
          }

          customerBasket.getPurchases().forEach(
              purchases -> purchases.setCostAfterDiscount(
                  // 99 cost
                  // 1 - 0.9
                  // 99 - round(1 / 100 * 99) = 99 - 1 = 98
                  purchases.getCost() - Math.round((float) discount / 100 * purchases.getCost())
              )
          );

          return discount;
        }
    );
  }
}
```

### hands on unit tests - without import of testing libs

```java
package ru.anvera.service;

import ru.anvera.models.CustomerBasket;
import ru.anvera.models.Purchase;

import java.util.ArrayList;
import java.util.List;


class DiscountServiceTest {
  private static DiscountService discountService;

  public static void setUp() {
    discountService = new DiscountService(new AsyncDiscountServiceCallerImpl());
  }


  public static void main(String[] args) {
    setUp();
    testAppliedDiscount();
    testNonAppliedDiscount();
    testComplexdDiscount();
  }

  private static void testComplexdDiscount() {
    // arrange
    List<Purchase> purchaseList = new ArrayList<>();
    purchaseList.add(new Purchase(
        444L,
        "book",
        99,
        99
    ));

    CustomerBasket customerBasket = new CustomerBasket(
        123L,
        3L,
        purchaseList
    );

    // act
    discountService.applyDiscount(customerBasket);

    // assert
    // costAfterDiscount 1000
    if (customerBasket.getPurchases().get(0).getCostAfterDiscount() != 98) {
      throw new RuntimeException("expected discount should not be applied!");
    }
    System.out.println(customerBasket.getPurchases().get(0).getCostAfterDiscount());
  }

  private static void testNonAppliedDiscount() {
    // arrange
    List<Purchase> purchaseList = new ArrayList<>();
    purchaseList.add(new Purchase(
        444L,
        "book",
        1000,
        1000
    ));

    CustomerBasket customerBasket = new CustomerBasket(
        123L,
        100L,
        purchaseList
    );

    // act
    discountService.applyDiscount(customerBasket);

    // assert
    // costAfterDiscount 1000
    if (customerBasket.getPurchases().get(0).getCostAfterDiscount() != 1000) {
      throw new RuntimeException("expected discount should not be applied!");
    }
  }

  private static void testAppliedDiscount() {
    // arrange
    List<Purchase> purchaseList = new ArrayList<>();
    purchaseList.add(new Purchase(
        444L,
        "book",
        1000,
        1000
    ));

    CustomerBasket customerBasket = new CustomerBasket(
        123L,
        1L,
        purchaseList
    );

    // act
    discountService.applyDiscount(customerBasket);

    // assert
    // costAfterDiscount 900
    if (customerBasket.getPurchases().get(0).getCostAfterDiscount() != 900) {
      throw new RuntimeException("expected discount was not applied!");
    }
  }
}

```
