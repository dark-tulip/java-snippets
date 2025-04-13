package kz.lab.task2;

import kz.lab.annotations.Cached;
import kz.lab.annotations.Setter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class CachingProxyV2 {

  /**
   * Берет класс объекта и возвращает
   * новый проксируемый инстанс (в котором работает интерцептор для аннотаций @Setter @Cached)
   */
  public static <T> T cache(T obj) {
    try {
      // взять класс объекта
      Class<?> clazz = obj.getClass();

      // новый проксируемый инстанс БайтБадди,
      // который перехватывает наши аннотации @Setter @Cached во время вызова методов
      return (T) new ByteBuddy()
          .subclass(clazz)
          .method(ElementMatchers.any())
          .intercept(MethodDelegation.to(new CacheInterceptor()))
          .make()
          .load(clazz.getClassLoader())
          .getLoaded()
          .getDeclaredConstructor()
          .newInstance();

    } catch (Exception e) {
      throw new RuntimeException("Failed to create cached proxy", e);
    }
  }


  /**
   * В этом классе реализуем логику перехватчика методов
   */
  public static class CacheInterceptor {
    private final Map<MethodKey, Object> cache = new HashMap<>();

    private boolean objectStateChanged = false;

    public CacheInterceptor() {
    }

    @RuntimeType
    public Object intercept(@Origin Method method,
                            @AllArguments Object[] args,
                            @SuperCall Callable<Object> superCall) throws Exception {
      // При наличии аннотации пометить что
      // 1. состояние объекта изменено
      // 2. Вызвать основной метод
      if (method.isAnnotationPresent(Setter.class)) {
        objectStateChanged = true;
        return superCall.call();
      }

      if (method.isAnnotationPresent(Cached.class)) {
        // Взять название метода
        MethodKey key = new MethodKey(method, args);

        // Если состояние не изменено и наш метод помечен анноташкой
        if (!objectStateChanged && cache.containsKey(key)) {
          // взять return из кэша
          System.out.print(" == get from cache " + method.getName() + " ::");
          return cache.get(key);
        } else {
          // вызвать ориг метод
          Object result = superCall.call();

          // положить результат
          cache.put(key, result);

          // сбросить состояние объекта
          objectStateChanged = false;
          return result;
        }
      }

      // Обычный метод
      return superCall.call();
    }
  }

  /**
   * Коробка для метода и его параметров
   */
  private record MethodKey(Method method, Object[] args) {
    private MethodKey(Method method, Object[] args) {
      this.method = method;
      this.args   = args != null ? args.clone() : new Object[0];
    }

    @Override
    public int hashCode() {
      return Objects.hash(method, Arrays.deepHashCode(args));
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof MethodKey other)) return false;
      return method.equals(other.method) && Arrays.deepEquals(args, other.args);
    }
  }
}
