package kz.lab.task2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CachingProxyV2Test {
  private Child cached;

  @BeforeEach
  void setup() {
    Child cachingObject = new Child();
    cachingObject.setValue("Hello");
    cached = CachingProxyV2.cache(cachingObject);
  }

  @Test
  void testCachingWorks() {
    String result1 = cached.methodCached();
    String result2 = cached.methodCached();
    assertEquals(result1, result2, "Должно быть одинаковым из кэша");
  }

  @Test
  void testCacheNotUsedIfNoAnnotation() {
    String result1 = cached.method(); // переопределён, без @Cached
    String result2 = cached.method();
    assertNotEquals(result1, "in stdout не должно быть кэширования: get from cache");
    assertEquals(result1, result2);
    // Просто проверка, что метод каждый раз вызывается (вывод будет на экран)
  }

  @Test
  void testCacheInvalidatesIfSetterExists() {
    String before = cached.methodCached();
    cached.setValueWithCacheEvict("new value");
    String after1 = cached.methodCached();
    String after2 = cached.methodCached();
    assertNotEquals(before, after1, "Кэш должен сброситься после Setter");
    assertEquals(before,"[init]");
    assertEquals(after1,"[new value]");
    assertEquals(after1, after2);
  }

  @Test
  void testCacheNotEvictsIfSetterNotExists() {
    String first = cached.methodCached();
    cached.setValue("new value"); // there is @Setter on this method
    String second = cached.methodCached();
    assertEquals(first, second, "Кэш НЕ должен сбрасываться без @Setter");
  }
}
