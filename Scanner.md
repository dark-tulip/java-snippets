- сканнер нужно закрывать
- задать разделитель для разбиения по токенам `useDelimeter()`


``` Java
public class ScannerEx {

  public static void main(String[] args) {
    Scanner sc = new Scanner("Hello, I am here");
    System.out.println("sc.delimiter(): " + sc.delimiter());  // \p{javaWhitespace}+
    System.out.println("sc.locale(): " + sc.locale());        // ru_KZ

    System.out.println(sc.tokens().collect(Collectors.toList()));  // stream of tokens: [Hello,, I, am, here]

    sc.useDelimiter("\\W+");      // only readable letters, without any punctuation marks
    while (sc.hasNext()) {
      System.out.println(sc.next());
    }

    sc.reset();

    sc = new Scanner("A0000A AAAAA A B C");
    System.out.println(sc.nextInt(16));  // 10485770
    System.out.println(sc.nextInt(16));  // 699050
    System.out.println(sc.nextInt(16));  // 10
    System.out.println(sc.nextInt(16));  // 11
    System.out.println(sc.nextInt(16));  // 12
  }
}
```

#### Вывести все уникальные слова в отсортированном порядке
``` Java
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class ScannerEx {

  public static void main(String[] args) {
    String text = " 32432 Tell me something, boy\n" +
      "Aren't you tired tryna fill that void?\n" +
      "Or do you need more?\n" +
      "Ain't it hard keepin' it so hardcore?\n" +
      "\n" +
      "I'm falling\n" +
      "In all the good times, I find myself longing\n" +
      "For change\n" +
      "And, in the bad times, I fear myself\n" +
      "\n" +
      "I'm off the deep end, watch as I dive in\n" +
      "I'll never meet the ground\n" +
      "Crash through the surface where they can't hurt us\n" +
      "We're far from the shallow now";

    Scanner sc = new Scanner(text);

    sc.useDelimiter("\\W+");      // only readable letters, without any punctuation marks

    // values in treeSet are always ordered, содержит уникальные значения и только упорядоченно
    TreeSet<String> orderedWords = new TreeSet<>();
    TreeMap<String, Integer> orderedWordsMap = new TreeMap<>();

    while (sc.hasNext()) {
      String word = sc.next();
      orderedWords.add(word);
      if (orderedWordsMap.containsKey(word)) {
        orderedWordsMap.replace(word, orderedWordsMap.get(word) + 1);
      } else {
        orderedWordsMap.put(word, 1);
      }
    }

    for (var word : orderedWords) {
      System.out.println(word);
    }

    for (var word : orderedWordsMap.entrySet()) {
      System.out.println(word.getKey() + ": " + word.getValue());
    }
  }
}
```
Output
```
32432: 1
Ain: 1
And: 1
Aren: 1
Crash: 1
For: 1
I: 6
In: 1
Or: 1
Tell: 1
We: 1
all: 1
as: 1
bad: 1
boy: 1
can: 1
change: 1
deep: 1
dive: 1
do: 1
end: 1
falling: 1
far: 1
fear: 1
fill: 1
find: 1
from: 1
good: 1
ground: 1
hard: 1
hardcore: 1
hurt: 1
in: 2
it: 2
keepin: 1
ll: 1
longing: 1
m: 2
me: 1
meet: 1
more: 1
myself: 2
need: 1
never: 1
now: 1
off: 1
re: 1
shallow: 1
so: 1
something: 1
surface: 1
t: 3
that: 1
the: 6
they: 1
through: 1
times: 2
tired: 1
tryna: 1
us: 1
void: 1
watch: 1
where: 1
you: 2
```
