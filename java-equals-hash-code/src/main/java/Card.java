import java.util.Date;


public final class Card {

    String cardId;
    CardUser owner;
    Date expiredDate;

    public Card(String cardId, CardUser owner, Date expiredDate) {
        this.cardId = cardId;
        this.owner = owner;
        this.expiredDate = expiredDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                          // Указание на один участок в памяти
        if (o == null) return false;                         // если object is null - сразу ясно - не равны. Для любого объекта проверка a.equals(null) должна возвращать false
        if (o.hashCode() != this.hashCode()) return false;   // если разные хэш коды - сразу ясно - не равны
        if (o.getClass() != this.getClass()) return false;   // если разные классы - сразу ясно - не равны

        Card card = (Card) o;

        return cardId.equals(card.cardId)
                && owner.equals(card.owner)
                && expiredDate.equals(card.expiredDate);
    }

    @Override
    public int hashCode() {
        int result = cardId == null ? 0 : cardId.hashCode();
        result = result + 31 * (expiredDate != null ? expiredDate.hashCode() : 0);
        result = result + 31 * (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
