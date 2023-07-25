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
- `\b` - граница слова или числа
- `\B` - НЕ граница слова или числа


#### количество повторений
- `(value)?` - 0 или 1 повторение
- `(value)*`- 0 или много повторений
- `(value)+` - 1 точное повторение
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
- `value{n,}` - n или более раз

