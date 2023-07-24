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
- `[abc]` - а или b или с
- `[a-c]` - одна буква из переданного диапазона
- `[A-Fd-f1-9]` - одна буква или цифра в переданном  (совмещение диапазона)
- `a|b` - либо a либо b
- `[^A-S]` - соответствует одной букве НЕ из диапазона, начинается с символа карет, что значит отрицание диапазона
- `[.]` - один любой символ (НЕ новая строка)
- `^startsSame` - соотв началу строки
- `endWith$` - заканчивается на 

#### метасимволы
- `\d` - одна цифра
- `\D` - одна НЕ цифра
- `\w` - один буква, цифра или "_", `[A-Za-z1-9_]`
- `\W` - символ НЕ буква, НЕ цифра и не "_"
- `\s` - пробельный символ [\t\n\r\f]
- `\S` - НЕ пробельный символ
- `\A` - выражение в начале string-a
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

