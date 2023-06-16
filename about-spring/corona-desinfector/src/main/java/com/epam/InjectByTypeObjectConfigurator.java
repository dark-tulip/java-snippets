package com.epam;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class InjectByTypeObjectConfigurator implements ObjectConfigurator {
  @Override
  @SneakyThrows
  public void configure(Object t, ApplicationContext context) {
    for(Field field : t.getClass().getDeclaredFields()) {
      if(field.isAnnotationPresent(InjectByType.class)) {
       field.setAccessible(true);
       Object object = context.getObject(field.getType());
       field.set(t, object);
      }
    }
  }
}
