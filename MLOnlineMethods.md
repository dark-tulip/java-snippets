# Задача предсказаний с использованием эксперных стратегий
- Е - мно-во экспертов
- D - мно-во пресказаний
- l: DxY -> [0,1] - функция потерь

Для t=1,...T. Эти четыре пункта называются раундом.
1) прирога генерирует исход y_t но не раскрывает его
3) эксперты делают предсказание {P -> t : E -> Epsylon}
4) предсказатель предсказывает P^t
5) природа раскрывает исход который она сгенерировала y_t
- предсказание несет потери l (p_t, y_t)
- эксп Е -> epsylon - l(P E,t, yt)

Цель. Сделать regret как можно меньше.
Регрет R_T = L_T - min L (E, T)
L_T = SUM t=[1/T] (l (P T, y_t)) -
L_E,T = SUM t=[1/T] (l (P_(E,T), y_t)) - кумулятивные потери

### Алгоритм Halving 
l (p,y) = II (p!=y) исходы либо ноль либо один. y1,...,y_T = {0, 1}

Cчитаем что есть эксперт который всегда предсказывает верно `E*`
Е = {1,..,N} - мн-во экспертов

Алгоритм предсказаний:
1) init всем по единиче (в начальный момент вес каждого эксперта равен единице)
2) P^t = { 0 если вес эксперта занулился, он не участвует в голосовании ;; 1 иначе }
3) если мы ошиблись, то всему присваиваем нулевой вес
Задача. Поскорее найти идеального эксперта.

Нам нужно понять сколько раз мы ошибемся при такой стратегии. 
Теория. При сформ предположениях регрет алг Halving не превосходит [log_2N]
если мы ошиблись сумма весов уменьшается в два раза. 
При этом W_t >=1 есть и будет эксперт который никогда не ошибается и сумма весов. 
Эта задача не работает и пропадает когда у нас нет ижеального эксперта. Нужно не обнулять вес эксперта за каждую ошибку, а дисконтировать его.
- в этом алгоритме, одна ошибка сразу же зануляет вес эксперта.
- пример игрушечный

### Алгоритм экспоненциального взвешивания
- У - произвольное мно-во
- Д - произвю вып мн.
- l(p,y) - вып по 1 аргументу при каждом y -> Y
E = {1, ..., N} - мн-во экспертов конечно
Дано:
1) {w i_0, i <= i <= N} - начальные веса эксп; ню - параметр
2) Для t=1,2...:
- делаем средневзвешанное предсказание
- веса пересчитываются умножением на такую экспоненту

**Лемма Хёффдинга.** Если случайная велична кси лежит в отрезке от а до в, то 
Теорема. Пусть l(p,y) принимает значения из отрезка [0, 1] и выпукла по первому аргрументу для каждого y. 
Сумма всех весов на каждом шаге не больше единицы и при этом все веса не отризательны. Тогда для регрета алг эксп взвеш удовл мн-ву R.
Замечания:
1) Если в начальный момент времени все веса равны: w_0 = 1/N то Регрет растет как O(sqrt(T)). Регрет в среднем на один раунд убывает.

Применение. Распределентие активов в портфеле.
- вес уменьшается если цена проседает. Алгоритм Хэш - и алгоритм экспоненциального взвешивания.
- указать множество


