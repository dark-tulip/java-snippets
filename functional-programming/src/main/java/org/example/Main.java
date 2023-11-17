package org.example;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {

        Function<Integer, Integer> incrementor = x -> x + 1;
        Function<Integer, Integer> multiplier = x -> x * 10;

        System.out.println(incrementor.apply(3));
        System.out.println(multiplier
                .andThen(Main::inc));

    }

    static Function<Integer, Integer> multiply(Integer intNum) {
        return n -> intNum * 10;
    }

    static Function<Integer, Integer> inc(Integer intNum) {
        return n -> intNum * 10;
    }
}