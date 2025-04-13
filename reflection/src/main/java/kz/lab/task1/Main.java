package kz.lab.task1;
public class Main {
    public static void main(String[] args) {
      CacheableMethod cacheableMethod       = new CacheableMethodImpl();
      CacheableMethod cachedCacheableMethod = CachingProxy.cache(cacheableMethod);

      System.out.println("== Первый вызов:");
      System.out.println(cachedCacheableMethod.method()); // вызов оригинального метода

      System.out.println("== Второй вызов:");
      System.out.println(cachedCacheableMethod.method()); // должно взять из кэша

      System.out.println("== Обновили состояние закэшированного значения:");
      cachedCacheableMethod.setValue("Установили Новое значение");

      System.out.println("== Третий вызов:");
      System.out.println(cachedCacheableMethod.method()); // снова вызов оригинального метода

      System.out.println("== Четвертый вызов:");
      System.out.println(cachedCacheableMethod.method()); // снова из кэша
    }

}
