package com.epam;

public interface ProxyConfigurator {
  Object replaceWithProxyIfNeeded(Object t, Class implClass);
}
