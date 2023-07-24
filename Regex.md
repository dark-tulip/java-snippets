### Регулярные выражения
- для вставки поиска и замены подстроки по шаблону
- вывод совпадения - метод group из матчера
- производительность с классами Pattern and Matcher будет в разы выше (чем поиск с методами класса String)
```Java
 public static void main(String[] args) {
    String str = "AAAA";
    Pattern pattern = Pattern.compile("AA");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      System.out.println(matcher.group());
    }
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
- `\w` - один буква, цифра или "_"
- `\W` - символ НЕ буква, НЕ цифра и не "_"
- `\s` - пробельный символ
- `\S` - НЕ пробельный символ
- `\A` - выражение в начале string-a
- `\Z` - выражение в конце string-a
- `\b` - граница слово или числа
- `\B` - НЕ граница слова или числа


#### количество повторений
- `value?` - 0 или 1 повторение
- `value*`- 0 или много повторений
- `value+` - 1 точное повторение
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

