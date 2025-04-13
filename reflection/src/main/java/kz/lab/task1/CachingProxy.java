package kz.lab.task1;

import kz.lab.annotations.Cached;
import kz.lab.annotations.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class CachingProxy {

  @SuppressWarnings("unchecked")
  public static <T> T cache(T target) {
    Class<?>[] interfaces = target.getClass().getInterfaces();

    Map<Method, Object> cache    = new HashMap<>();
    final boolean[]     modified = {false};  // это массив потому что нам нужен final

    return (T) Proxy.newProxyInstance(
        target.getClass().getClassLoader(),
        interfaces,
        (Object proxy, Method method, Object[] args) -> {
          Method originalMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

          // Если метод помечен как @Setter — сбрасываем кэш
          if (originalMethod.isAnnotationPresent(Setter.class)) {
            modified[0] = true;
            return method.invoke(target, args);
          }

          // Если метод НЕ помечен @Cached — обычный вызов
          if (!originalMethod.isAnnotationPresent(Cached.class)) {
            return method.invoke(target, args);
          }

          // Если уже есть в кэше и объект не был модифицирован
          if (cache.containsKey(method) && !modified[0]) {
            System.out.println(" == get result from cache");
            return cache.get(method);
          }

          // Иначе — вызов метода, сохранение результата
          Object result = method.invoke(target, args);
          cache.put(method, result);
          modified[0] = false;
          return result;
        }
    );
  }
}
