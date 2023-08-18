### Регулярные выражения
- для вставки поиска и замены подстроки по шаблону
- `matcher.group()`
- `matcher.start()` - позиция начала шаблонного вхождения
- вывод совпадения - метод group из матчера
- производительность с классами Pattern and Matcher будет в разы выше (чем поиск с методами класса String)
```Java
public class PatternMatcherRegex {
  public static void findRegex(String str, Pattern pattern) {
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      System.out.println("Position: " + matcher.start() + " end: " + matcher.end() + ", found: " + matcher.group());
    }
  }
  public static void main(String[] args) {
    findRegex("AAAAABBBB", Pattern.compile("AB{3}"));
  }
  
  // Position: 5 end: 9, found: ABBB
}
```
### regex cheat sheet

- `abc` - точное вхождение
```Java
findRegex("AAAAABBBBCABC", Pattern.compile("ABC"));
// Position: 10 end: 13, found: ABC
```
- `[abc]` - а или b или с
```Java
findRegex("AAASSBBAC", Pattern.compile("[ABC]"));
/*
Position: 0 end: 1, found: A
Position: 1 end: 2, found: A
Position: 2 end: 3, found: A
Position: 5 end: 6, found: B
Position: 6 end: 7, found: B
Position: 7 end: 8, found: A
Position: 8 end: 9, found: C
*/
```
- `[a-c]` - одна буква из переданного диапазона
```Java
findRegex("FFFDbdC", Pattern.compile("[a-d]"));
/*
Position: 4 end: 5, found: b
Position: 5 end: 6, found: d
*/
```
- `[A-Fd-f1-9]` - одна буква или цифра в переданном  (совмещение диапазона)
```Java
findRegex("FFFDbdC", Pattern.compile("[a-dB-D]"));
/*
Position: 3 end: 4, found: D
Position: 4 end: 5, found: b
Position: 5 end: 6, found: d
Position: 6 end: 7, found: C
*/
```
- `a|b` - либо a либо b
```Java
findRegex("FFFDbdCgf", Pattern.compile("[asd|C]"));
/*
Position: 5 end: 6, found: d
Position: 6 end: 7, found: C
*/
```
- `[^A-S]` - соответствует одной букве НЕ из диапазона, начинается с символа карет, что значит отрицание диапазона
```Java
findRegex("asdDC", Pattern.compile("[^asd|C]"));
/*
Position: 3 end: 4, found: D
*/
```
- `[.]` - один любой символ (НЕ новая строка)
```Java
findRegex("A\nS\rF\t", Pattern.compile("."));
/*
Position: 0 end: 1, found: A
Position: 2 end: 3, found: S
Position: 4 end: 5, found: F
Position: 5 end: 6, found: 	   // here is \t
*/
```
- `^startsSame` - соотв началу строки
```Java
findRegex("ABCASD AD AF ABHH\n\r\f\sAB", Pattern.compile("^AB"));
/*
Position: 0 end: 2, found: AB
*/
```
- `endWith$` - заканчивается на 
```Java
findRegex("ABCASD AD AF ABHHAB\n\r\f\sAB", Pattern.compile("AB$"));
/*
Position: 23 end: 25, found: AB
*/
```
#### метасимволы
- `\d` - одна цифра
```Java
findRegex("AS1AS123", Pattern.compile("\\d\\d"));
/*
Position: 5 end: 7, found: 12
*/
```
- `\D` - одна НЕ цифра
```Java
findRegex("ASz1AS!123", Pattern.compile("\\D\\D\\D"));
/*
Position: 0 end: 3, found: ASz
Position: 4 end: 7, found: AS!
*/
```
- `\w` - один буква, цифра или "_", `[A-Za-z1-9_]`
```Java
findRegex("ASz1AS!123", Pattern.compile("\\w\\w\\w"));
/*
Position: 0 end: 3, found: ASz
Position: 3 end: 6, found: 1AS
Position: 7 end: 10, found: 123
*/
```
- `\W` - символ НЕ буква, НЕ цифра и не "_"
```Java
findRegex("ASz1AS!? $5432@#%123", Pattern.compile("\\W\\W\\W"));
/*
Position: 6 end: 9, found: !?    // Тут есть пробел
Position: 14 end: 17, found: @#%/*
Position: 5 end: 7, found: 12
*/
```
- `\s` - пробельный символ [\t\n\r\f]
```Java
findRegex("  \n@\r#%\t12\s3", Pattern.compile("\\s"));
/*
Position: 6 end: 9, found: !?    // Тут есть пробел
Position: 14 end: 17, found: @#%/*
Position: 5 end: 7, found: 12
*/
```
- `\S` - НЕ пробельный символ
```Java
findRegex("  \n@\r#%\t2\s", Pattern.compile("\\S"));
/*
Position: 6 end: 9, found: !?    // Тут есть пробел
Position: 14 end: 17, found: @#%/*
Position: 5 end: 7, found: 12
*/
```
- `\A` - выражение в начале string-a
```Java
findRegex("QDSQAZ\sQAZ", Pattern.compile("\\AQAZ"));
// empty
```
- `\Z` - выражение в конце string-a
```Java
findRegex("QDSQAZQAZQAZ", Pattern.compile("QAZ\\Z"));
findRegex("QDSQAZQAZQAZ\n", Pattern.compile("QAZ\\Z"));
/*
Position: 9 end: 12, found: QAZ
Position: 9 end: 12, found: QAZ (не учитывает перенос строки)
*/
```
- `\b` - граница слова или числа
```Java
findRegex("1234567890 122 66 876 9090 00", Pattern.compile("\\b\\d\\d\\b"));
/*
Position: 15 end: 17, found: 66
Position: 27 end: 29, found: 00
*/
```
- `\B` - НЕ граница слова или числа
```Java
findRegex("1234567890 122 66 876 9090 00", Pattern.compile("\\B\\d{3}\\B"));
/*
Position: 1 end: 4, found: 234
Position: 4 end: 7, found: 567
*/
```

#### количество повторений
- `(value)?` - 0 или 1 повторение
```Java
findRegex("folder holder older elder oldf eld oper ho-ho!", Pattern.compile("ho(lder)?"));
/*
Position: 7 end: 13, found: holder
Position: 40 end: 42, found: ho
Position: 43 end: 45, found: ho
*/
```
- `(value)*`- 0 или много повторений
```Java
findRegex("folder holder older elder oldf eld oper ho-ho!", Pattern.compile("ho(lder)*"));
/*
Position: 7 end: 13, found: holder
Position: 40 end: 42, found: ho
Position: 43 end: 45, found: ho
*/
```
- `(value)+` - 1 точное повторение
```Java
findRegex("folder holder older elder oldf eld oper ho-ho! hold", Pattern.compile("ho(lder)+"));
/*
Position: 7 end: 13, found: holder
*/
```
- `B{n}` - точное повторение "B" n раз
```Java
    String str = "AAABABBB";
    Pattern pattern = Pattern.compile("AB{3}");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      System.out.println(matcher.group());
    }
  // Output: ABBB
```
- `value{n, m}` - от n до m раз
```Java
findRegex("ASD AES ASSASASS", Pattern.compile("(AS){1,2}"));
/*
Position: 0 end: 2, found: AS
Position: 8 end: 10, found: AS
Position: 11 end: 15, found: ASAS
*/
```
- `value{n,}` - n или более раз
```Java
findRegex("ASD AES ASSASASS", Pattern.compile("AS{2,}"));
/*
Position: 8 end: 11, found: ASS
Position: 13 end: 16, found: ASS
*/
```


### Java String matches and split accept RegEx
- matches - cтрока целиком совпадает регулярному выражению
```Java
    String s1 = "t@mail.ru";
    System.out.println(s1.matches("\\w+@\\w+.ru"));  // true

    String s2 = "t@mail.ru t@mail.ru";
    System.out.println(s2.matches("\\w+@\\w+.ru"));  // false
```
## RegEx for IP address
```Java
    String pattern = "(([0-1]?\\d?\\d|2[0-5]{2}).){3}([0-1]?\\d?\\d|2[0-5]{2})";

    System.out.println(Pattern.matches(pattern, "255.255.255.255"));  // true
    System.out.println(Pattern.matches(pattern, "255.255.256.255"));  // false
    System.out.println(Pattern.matches(pattern, "0.0.0.0"));          // true
    System.out.println(Pattern.matches(pattern, "000.00.0.000"));     // true
    System.out.println(Pattern.matches(pattern, "000.00.0.0000"));    // false
```
- matcher 
- Каждая группа помещается в скобки `()` внутри регулярного выражения
- метод `group(0)` по умолчанию выводит всю переданную Матчеру строку
- если `group(number)` передать число - выведит группу из строки, которая разделена ()
- `lookingAt()` не имеет конца, каждый раз начинает поиск с начала строки (может застрять с бесконечном цикле)
- `find()` - каждый раз смещается по позиции, тем самым имея конечный цикл
- `replaceAll()` - заменяет каждую подпоследовательность в виде переданного шаблона
```Java
 String bankCardTotalString =
        "11112222333344442807425;\n" +
        "11112222333344442807425;\n" +
        "43564634563456345645645";
    Pattern pattern = Pattern.compile("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})(\\d{2})(\\d{3})");
    Matcher matcher = pattern.matcher(bankCardTotalString);
    System.out.println(matcher.find());

    String formattedResult = matcher.replaceAll("CSV:($7) MM/DD:$5/$6 CODE:$1-$2-$3-$4");
    System.out.println(formattedResult);

    matcher.reset();

    // find изменяет состояние матчера
    while (matcher.find()) {
      System.out.println(matcher.group(1));
    }

    matcher.reset();

    while (matcher.find()) {
      System.out.println(matcher.group(0));
    }

    while (matcher.lookingAt()) {
      System.out.println("dsf: " + matcher.group(0));
      Thread.sleep(300);
    }
/*
Output:
true
CSV:(425) MM/DD:28/07 CODE:1111-2222-3333-4444;
CSV:(425) MM/DD:28/07 CODE:1111-2222-3333-4444;
CSV:(645) MM/DD:56/45 CODE:4356-4634-5634-5634
1111
1111
4356
11112222333344442807425
11112222333344442807425
4356463453456345645645
dsf: 11112222333344442807425
... inf
dsf: 11112222333344442807425
*/
```

### Шаблон спецификатора формата
** %[flag][width][.precision]datatype_specifier **
- flag
  - `0` - заполнить нулями (число)
  - `,` - разделитель разрядов в числах 
  - `-` - выровнить слева
- width - продолжительность флага
- precision - дробная точность
  
###### data_specifier
- b - boolean
- s - string 
- c - char
- d - digit
- f - float number

```Java
    System.out.printf("%03d \n", 1234);             // 1234
    System.out.printf("%.1f \n", 1234.234324);      // 1234,2
    System.out.printf("%,.1f\n", 1234.234324);      // 1 234,2
    System.out.printf("%c.%c.%c\n", 66, 67, 68);    // B.C.D
    System.out.printf("%05d\n", 44);                // 00044
    System.out.printf("%8d\n", 44);                 //       44
    System.out.printf("%-8d\n", 44);                // 44  
    System.out.printf("%b\n", 1);    // true
    System.out.printf("%b\n", -1);   // true
    System.out.printf("%b\n", 0);    // true
    System.out.printf("%b\n", null); // false
    System.out.printf("%n");  // new line
```

### Dates
```
    Date date = new Date();
    System.out.printf("%tH %n", date);  // hours now
    System.out.printf("%tM %n", date);  // minutes now
    System.out.printf("%tS %n", date);  // seconds now
    System.out.printf("%ts %n", date);  // total seconds

    System.out.printf("%tD %n", date);  // 07/29/23 (month/day/year)
    System.out.printf("%tT %n", date);  // 22:20:14
    System.out.printf("%tz %n", date);  // +0600 (timezone)
    System.out.printf("%tZ %n", date);  // ALMT
    System.out.printf("%tp %n", date);  // pm or am
    
    System.out.printf("%ta %n", date);  // сб (short week name)
    System.out.printf("%tA %n", date);  // суббота (total week name)
    System.out.printf("%tb %n", date);  // июл. (short month name)
    System.out.printf("%tB %n", date);  // июля (total month name)
    System.out.printf("%td %n", date);  // 29 (day of month)
    System.out.printf("%tm %n", date);  // 07 (month in digits)
    System.out.printf("%tY %n", date);  // 2023
    System.out.printf("%ty %n", date);  // 23 (last two digits of year)
```
### Hex, Oct, Bin
```Java
    System.out.printf("%H %n", 999);  // 3E7  - hex
    System.out.printf("%h %n", 999);  // 3e7  - hex
    System.out.printf("%o %n", 999);  // 1747 - oct

    String fillWithZeros = String.format("%10s%n",Integer.toBinaryString(99)).replaceAll(" ", "0");
    System.out.printf(fillWithZeros);                           // 0001100011
    System.out.printf("%10s%n", Integer.toBinaryString(99));    //    1100011
    System.out.printf("%-10s%n", Integer.toBinaryString(99));   // 1100011
```
### Regex for replace sources inside image tag
- `?:` это игнорироват группу вхождений
- `+` - обязательное вхождение
```Java
    final String SEARCH_PATTERN = "src=\"https?://(?:[.a-z\\d]+/+)+([-.a-z\\d]+)\"+";

    String str1 = "<img src=\"http://cdn.pixabay.com/photo/2015/04/23/22/tree-7368851280.jpeg\">";
    Pattern pattern = Pattern.compile(SEARCH_PATTERN);
    Matcher matcher = pattern.matcher(str1);
    String str = matcher.replaceAll("src=\"[fileId:$1]\"");
    
    System.out.println(str);  // <img src="[fileId:tree-7368851280.jpeg]">
```
