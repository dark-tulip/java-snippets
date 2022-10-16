import java.util.Date;


public class CardUser {

    String userId;
    String name;
    String surname;
    Date birthDate;

    public CardUser(String userId, String name, String surname, Date date) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.birthDate = date;
    }

    public CardUser() {

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;                          // Указание на один участок в памяти
        if (o == null) return false;                         // если object is null - сразу ясно - не равны. Для любого объекта проверка a.equals(null) должна возвращать false
        if (o.hashCode() != this.hashCode()) return false;   // если разные хэш коды - сразу ясно - не равны
        if (o.getClass() != this.getClass()) return false;   // если разные классы - сразу ясно - не равны

        CardUser user = (CardUser) o;

        return userId.equals(user.userId)
                && name.equals(user.name)
                && userId.equals(user.surname)
                && birthDate.equals(user.birthDate);

    }

    @Override
    public int hashCode() {
        int result = (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);

        return result;
    }
}
