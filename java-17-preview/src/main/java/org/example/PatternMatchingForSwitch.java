package org.example;

/**
 * Паттерны с гвардами (условие внутри ветки)
 * определение типа в блоке switch
 * Дружелюбен к null
 * it is a preview feature in Java 17, so it would not be compiled
 * released on 21 Java
 */
public final class PatternMatchingForSwitch {
    public static void main(String[] args) {
        Object object = "Hello";

//        object = switch (object) {
//            case "Hello" -> "Here is hello";
//            case 123 -> "What is integer";
//            default -> throw new IllegalStateException("Unexpected value: " + object);
//        };

        System.out.println(object);
    }
}
