package org.example;

import java.util.List;

public class StreamOfList {
    public static void main(String[] args) {
        System.out.println(loadSpecialStrings(List.of("AAA", "asds", "gggg")));
    }

    static List<String> loadSpecialStrings(List<String> allStrings) {
        return allStrings.stream()
                .filter(x -> x.contains("a"))
                .toList();  //  toList() is faster than .collect(Collectors.toList())
    }

}
