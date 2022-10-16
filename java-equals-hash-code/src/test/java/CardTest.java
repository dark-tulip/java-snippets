import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void cardWithSameAttribs() {

        CardUser user = new CardUser();
        Date date = new Date();

        Card card1 = new Card("111", user, date);
        Card card2 = new Card("111", user, date);

        assertEquals(card1, card2);
    }

    @Test
    public void differentCardIds() {

        CardUser user = new CardUser();
        Date date = new Date();

        Card card1 = new Card("122", user, date);
        Card card2 = new Card("1222", user, date);

        assertNotEquals(card1, card2);
    }

    @Test
    public void differentUsersAreNotEquals() {

        CardUser user1 = new CardUser("222", "name", "surname", new Date());
        CardUser user2 = new CardUser("333", "name", "surname", new Date());

        Card card1 = new Card("111", user1, new Date());
        Card card2 = new Card("111", user2, new Date());

        System.out.println(card1);
        System.out.println(card2);

        System.out.println(new CardUser());
        System.out.println(new CardUser());

        System.out.println(user1);
        System.out.println(user2);

        assertNotEquals(card1, card2);

    }

    @Test
    public void sameIdsButDifferentAttrib() {

        CardUser user1 = new CardUser("222", "name", "surname", new Date());
        CardUser user2 = new CardUser("222", "name", "surname2", new Date());

        assertNotEquals(new Card("111", user1, new Date()), new Card("111", user2, new Date()));
    }

    @Test
    public void sameIdsWithSameUser() {

        CardUser user1 = new CardUser("222", "name", "surname", new Date());

        assertEquals(new Card("111", user1, new Date()), new Card("111", user1, new Date()));

    }

    @Test
    public void differentIdsAreNotSame() {

        CardUser user1 = new CardUser("222", "name", "surname", new Date());

        assertNotEquals(new Card("111", user1, new Date()), new Card("222", user1, new Date()));

    }
}
