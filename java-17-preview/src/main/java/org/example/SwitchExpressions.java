package org.example;

/**
 * Что нового?
 * 1) Return value в качестве yield
 * 2) можно не писать break
 * 3) перечислять атрибуты через запятую
 * Это не лямбды, а что то другое
 * Можно выражение,
 * Можно обычный программный блок
 * Нельзя сделать return c вышестоящего метода
 * Можно кидать исключение (в блок оборачивать не обязательно)
 */
public class SwitchExpressions {

    public static void main(String[] args) {
        Animals animals = Animals.CAT;
        System.out.println(testSwitchCase(animals));
    }

    public static String testSwitchCase(Animals animals) {
        String result = switch (animals) {
            case CAT, GOLDFISH -> {
                System.out.println("It will eat");
                yield "Hello, this is cat or goldfish";  // switch expression from 14th java can return the value
            }
            case DOG -> "This is dog or parrot";
            default -> throw new RuntimeException("Undefined animals behaviour");
        };

        result += " YES";

        return result;
    }
}
