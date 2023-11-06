package org.example;


/**
 * У рекорда обязательно должна быть шапка
 * поля финальные, не изменяемые
 * переопределен equals() and hashCode()
 * наследовать record запрещено
 * Лучше чем tuple()
 * @param x
 * @param y
 */
public record Point(int x, int y) {
    public Point {
        if (y < 0) {
            throw new IllegalArgumentException("Value 'y' cannot be less than 0");
        }
    }

    int getSum() {
        return x + y;
    }
}


class Test {
    /**
     * 1
     * 2
     * 33
     * Point
     * 3
     * Point[x=1, y=2]
     */
    public static void main(String[] args) {
        Point point = new Point(1, 2);
        System.out.println(point.x());
        System.out.println(point.y());
        System.out.println(point.hashCode());
        System.out.println(point.getClass().getSimpleName());
        System.out.println(point.getSum());
        System.out.println(point);

        // Exception in thread "main" java.lang.IllegalArgumentException: Value 'y' cannot be less than 0
        // Point point2 = new Point(1, -2);
    }
}