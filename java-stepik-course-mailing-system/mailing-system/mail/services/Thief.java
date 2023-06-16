package mail.services;

import mail.packages.Package;
import mail.types.MailPackage;
import mail.types.Sendable;

/*
3) Thief – вор, который ворует самые ценные посылки и игнорирует все остальное.
Вор принимает в конструкторе переменную int – минимальную стоимость посылки, которую он будет воровать.
Также, в данном классе должен присутствовать метод getStolenValue, который возвращает суммарную стоимость всех посылок,
которые он своровал. Воровство происходит следующим образом: вместо посылки, которая пришла вору,
он отдает новую, такую же, только с нулевой ценностью и содержимым посылки "stones instead of {content}".
 */
public class Thief implements MailService {

    private final int minCost;
    private static int totalStolen = 0;

    public Thief(int minCost) {
        this.minCost = minCost;
    }

    public int getStolenValue() {
        return totalStolen;
    }

    @Override
    public Sendable processMail(Sendable mail) {
        if (mail instanceof MailPackage) {
            Package pkg = ((MailPackage) mail).getContent();
            if (pkg.getPrice() >= minCost) {
                totalStolen += pkg.getPrice();
                return new MailPackage(
                        mail.getFrom(),
                        mail.getTo(),
                        new Package("stones instead of " + pkg.getContent(),  0));
            }
        }
        return mail;
    }
}
