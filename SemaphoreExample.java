import java.util.concurrent.Semaphore;

public class SemaphoreExample {

    public static void main(String[] args) {

        Semaphore restRoom = new Semaphore(2);
        new Person("name1", restRoom);
        new Person("name2", restRoom);
        new Person("name3", restRoom);
        new Person("name4", restRoom);
        new Person("name5", restRoom);
    }
}


class Person extends Thread {

    String name;

    private Semaphore restRoom;

    public Person(String name, Semaphore restRoom) {
        this.name = name;
        this.restRoom = restRoom;

        // при создании персоны сразу запускать тред (run task)
        this.start();
    }


    @Override
    public void run() {
        try {
            System.out.println(this.name + " is waiting");

            // ожидает попытки получения доступа к ресурсу
            // до этого времени поток заблокирован
            restRoom.acquire();
            sleep(1000);
            System.out.println(this.name + " in the room, available " + this.restRoom.availablePermits());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {

            // увеличить счетчик семафора на единицу,
            // тем самым разрешая следующему потоку получить доступ к ресурсу
            restRoom.release();
        }
    }
}
