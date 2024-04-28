### Что нового?

- избавляемся от get, используем accessors
- методы со строками
- Гварды внутри блока switch case - условия
- метод toList() в стримах
- Objects.checkFromIndexSize()
- RandomGenerator interface (more random)
- internal static class, enum or interface
- sealed classes
- faster flatMap realization -- TODO

## Switch Case
- time complexity for `switch` is `O(1)` whereas `if` is `O(n)`
- switch по типу данных
- поддержка гвардов (условий внутри switch)

## Sealed classes
- final class means that noone can extend this class,
- sealed class mean that only permitted classes can extend this class
