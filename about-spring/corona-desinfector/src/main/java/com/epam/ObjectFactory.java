package com.epam;

import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
  private final ApplicationContext context;
  private List<ObjectConfigurator> configurators = new ArrayList<>();
  private List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

  @SneakyThrows
  public ObjectFactory(ApplicationContext context) {
    this.context = context;
    for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
      configurators.add(aClass.getDeclaredConstructor().newInstance());
    }

    for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ProxyConfigurator.class)) {
      proxyConfigurators.add(aClass.getDeclaredConstructor().newInstance());
    }
  }

  @SneakyThrows  // сделать checked exception unchecked-ом
  public <T> T createObject(Class<T> implClass) {

    // создает метод
    T t = create(implClass);

    // настраивает
    configure(t);

    // донастраивает like BeanPostProcessor
    invokeInit(implClass, t);

    t = replaceWithProxyIfNeeded(implClass, t);

    // Возращает настроенный объект
    return t;
  }

  private <T> T replaceWithProxyIfNeeded(Class<T> implClass, T t) {
    for (ProxyConfigurator configurator : proxyConfigurators) {
      t = (T) configurator.replaceWithProxyIfNeeded(t, implClass);
    }
    return t;
  }

  private <T> void invokeInit(Class<T> implClass, T t) throws IllegalAccessException, InvocationTargetException {
    for (Method m : implClass.getMethods()) {
      if (m.isAnnotationPresent(PostConstruct.class)) {
        m.invoke(t);
      }
    }
  }

  private <T> void configure(T t) {
    configurators.forEach(objectConfigurator -> objectConfigurator.configure(t, context));
  }

  private <T> T create(Class<T> implClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return implClass.getDeclaredConstructor().newInstance();
  }
}
