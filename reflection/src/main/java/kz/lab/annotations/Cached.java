package kz.lab.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Если закэшированный метод вызывается повторно
 * и объект не был изменен с момента прошлого вызова –
 * то вместо вызова настоящего метода необходимо возвращается закэшированное.
 * Целью может быть МЕТОД
 * Доступна во время исполнения программы
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
}
