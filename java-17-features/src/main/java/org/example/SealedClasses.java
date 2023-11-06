package org.example;

/**
 * Должны быть в том же модуле
 * Конечный класс должен быть финальным
 */
sealed class SealedClass permits SomePermittedClass {
    public static void main(String[] args) {
        enum LocalEnum {ONE, TWO, THREE}

        System.out.println(LocalEnum.THREE);
        System.out.println(SomePermittedClass.class.getSimpleName());
    }
}


final class SomePermittedClass extends SealedClass {
    public SomePermittedClass() {
    }
}
/**
 * Диаграмма эйлера венна запрещающая наследование
 */
sealed interface AorBorC {
}

sealed interface AorB extends AorBorC {
}

sealed interface AorC extends AorBorC {
}

sealed interface BorC extends AorBorC {
}

final class A implements AorB, AorC {
}

final class B implements AorB, BorC {
}

final class C implements AorC, BorC {
}
