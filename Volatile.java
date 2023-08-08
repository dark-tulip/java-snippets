public class VolatileEx {

    volatile int  x = 0;  // если убрать volatile, нарушится принцип happens before, 
    int y = 0;


    public static void main(String[] args) throws InterruptedException {

        VolatileEx volatileEx = new VolatileEx();

        // по умолчанию каждый поток хранит данные в thread local хэше
        Thread thread1 = new Thread(() -> {
            System.out.println("Xl2XD9NAo3 :: " + Thread.currentThread().getName());
            // ТУТ оптимизатор может поменять порядок операций местами
            volatileEx.y = 2;
            volatileEx.x = 1;
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("71FrZ6FWSg :: " + Thread.currentThread().getName());
            if (volatileEx.x == 1) {
                volatileEx.y =  9;
            }
        });


        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // наглядный пример volatile, без него не соблюдается принцип happens before
        // все инструкции до записи в волатильную переменную выполняются ДО записи значения в переменную
        // volatile обеспечивает доступ ко чтению и записи напрямую из main memory
        // за счёт volatile мы достигнем одного и того же результата в нашем случае: x=1, y=9
        System.out.println("x=" + volatileEx.x + ", y=" + volatileEx.y);
        // x=1, y=2
        // x=1, y=9
    }
}
