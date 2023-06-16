package com.epam;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DeprecatedHandlerProxyConfiguratorImpl implements ProxyConfigurator {
  @Override
  public Object replaceWithProxyIfNeeded(Object t, Class implClass) {
    if (implClass.isAnnotationPresent(Deprecated.class)) {

      if(implClass.getInterfaces().length == 0) {
        return Enhancer.create(implClass, (net.sf.cglib.proxy.InvocationHandler) (proxy, method, args) ->
          getInvocationHandlerLogic(method, args, t));
      }

      // Возращаем не настроящий, а проксируемый объект
      return Proxy.newProxyInstance(implClass.getClassLoader(), implClass.getInterfaces(), (proxy, method, args) ->
        getInvocationHandlerLogic(method, args, t));
    }
    return t;
  }

  private static Object getInvocationHandlerLogic(Method method, Object[] args, Object t) throws IllegalAccessException, InvocationTargetException {
    System.out.println("***** Что же ты делаешь урод!!! **** ");
    return method.invoke(t, args);
  }
}
