## Acquire and Release
all the mem operations
- preceeding release before it starts
- following an acquire can't start before it completes

## Mutual Exclusion
- два потока не могут находиться в критической секции одновременно simultaneously
- Лок может обеспечить это состояние
- can be acquired (locked)
- and released (unlocked)

## Monitor = Lock + Condition Variable
может быть в одном из двух состояний
- **thin** - используются атомики чтобы залочить объект **atomics**
- **inflated** - потоки паркуются пока ожидают acquisition, **thread partking**
- **biased** - позволяет дешево лочить одним потоком, но обойдется дорого когда множество потоков попытаются залочить объект, **by one thread**
- 
