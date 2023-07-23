### Регульярные выражения
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

`abc` - точное вхождение
`[abc]` - а или b или с
`[a-c]` - одна буква из переданного диапазона
`[A-Fd-f1-9]` - одна буква или цифра в переданном  (совмещение диапазона)
`a|b` - либо a либо b
`[^A-S]` - соответствует одной букве НЕ из диапазона, начинается с символа карет, что значит отрицание диапазона
`[.]` - один любой символ (НЕ новая строка)
`^startsSame` - соотв началу строки
`endWith$` - заканчивается на 

метасимволы
