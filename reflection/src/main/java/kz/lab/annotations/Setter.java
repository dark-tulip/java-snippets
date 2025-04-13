package kz.lab.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Используйте, чтобы обновить закэшированное значение.
 * Целью может быть МЕТОД
 * Доступна во время исполнения программы.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Setter {}
