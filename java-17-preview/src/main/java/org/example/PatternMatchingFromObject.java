package org.example;

import java.util.Date;

public class PatternMatchingFromObject {

    public static void main(String[] args) {
        processValue("Hello");
        processValue(123);
        processValue(new Date());
    }

    public static void processValue(Object someObject) {
        if (someObject instanceof Number number) {
            System.out.println(number.getClass().getSimpleName());

        } else if (someObject instanceof Date date) {
            System.out.println(date.getClass().getSimpleName());

        } else if (someObject instanceof String str && !str.isEmpty()) {
            System.out.println(str.getClass().getSimpleName());
        }
    }

}
