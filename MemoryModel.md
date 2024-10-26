## Memory reordering types:
- LoadLoad - переупорядочивание чтений
- LoadStore - действия r, w могут выполниться в порядке w, r
- StoreLoad - действия w, r могут выполниться в порядке r, w
- StoreStore -  действия w1, w2 могут выполниться в порядке w2, w1

Memory model декларирует правила по которым возможно переупорядочивание. В зависимости от строгости возможна следующая согласованность
- **sequential consistency** - запрещено переупорядочивание
- **relaxed consistency **
- **weak consistency** - разрешены все переуполядочивания

## Memory orderign and Instructions reordering
- переупорядочивание инструкций процессова должно подчиняться правилам JMM



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
