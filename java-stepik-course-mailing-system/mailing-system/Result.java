public static class IllegalPackageException extends RuntimeException {
    public IllegalPackageException() {

    }
}

public static class StolenPackageException extends RuntimeException {
    public StolenPackageException() {

    }
}

/*
implement UntrustworthyMailWorker, Spy, Inspector, Thief,
StolenPackageException, IllegalPackageException as public static classes here
*/


/*
 1) UntrustworthyMailWorker – класс, моделирующий ненадежного работника почты,
 который вместо того, чтобы передать почтовый объект непосредственно в сервис почты,
 последовательно передает этот объект набору третьих лиц, а затем, в конце концов,
 передает получившийся объект непосредственно экземпляру mail_services. RealMailService.

 У mail_services. UntrustworthyMailWorker должен быть конструктор от массива mail_services. MailService
 (результат вызова processMail первого элемента массива передается на вход processMail второго элемента, и т. д.)
 и метод getRealMailService, который возвращает ссылку на внутренний экземпляр mail_services. RealMailService.
 */
public static class UntrustworthyMailWorker implements MailService {

    private static MailService[] workers;
    private static final RealMailService realWorker = new RealMailService();

    public UntrustworthyMailWorker(MailService[] ms) {
        workers = ms;
    }

    public MailService getRealMailService() {
        return realWorker;
    }

    @Override
    public Sendable processMail(Sendable mail) throws RuntimeException {
        for (MailService m : workers) {
            mail = m.processMail(mail);
        }
        return realWorker.processMail(mail);
    }
}


/*
2) Spy – шпион, который логгирует о всей почтовой переписке, которая проходит через его руки.
 Объект конструируется от экземпляра Logger, с помощью которого шпион будет сообщать о всех действиях.
 Он следит только за объектами класса mail_types.MailMessage и пишет в логгер следующие сообщения
 (в выражениях нужно заменить части в фигурных скобках на значения полей почты):
2.1) Если в качестве отправителя или получателя указан "Austin Powers", то нужно написать в лог
 сообщение с уровнем WARN: Detected target mail correspondence: from {from} to {to} "{message}"
2.2) Иначе, необходимо написать в лог сообщение с уровнем INFO: Usual correspondence: from {from} to {to}
 */
public static class Spy implements MailService {

    private static Logger logger = Logger.getLogger(MailMessage.class.getName());

    public Spy(Logger logger) {
        Spy.logger = logger;
    }

    @Override
    public Sendable processMail(Sendable mail) {
        if (mail instanceof MailMessage) {
            MailMessage msg = (MailMessage) mail;
            if (msg.getFrom().equals("Austin Powers") || msg.getTo().equals("Austin Powers")) {
                logger.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"", new Object[] {msg.getFrom(), msg.getTo(), msg.getMessage()});
            } else {
                logger.log(Level.INFO, "Usual correspondence: from {0} to {1}", new Object[] {msg.getFrom(), msg.getTo()});
            }
        }
        return mail;
    }
}


/*
3) Thief – вор, который ворует самые ценные посылки и игнорирует все остальное.
Вор принимает в конструкторе переменную int – минимальную стоимость посылки, которую он будет воровать.
Также, в данном классе должен присутствовать метод getStolenValue, который возвращает суммарную стоимость всех посылок,
которые он своровал. Воровство происходит следующим образом: вместо посылки, которая пришла вору,
он отдает новую, такую же, только с нулевой ценностью и содержимым посылки "stones instead of {content}".
 */
public static class Thief implements MailService {

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


/*
4) Inspector – Инспектор, который следит за запрещенными и украденными посылками и
бьет тревогу в виде исключения, если была обнаружена подобная посылка. Если он заметил запрещенную посылку
с одним из запрещенных содержимым ("weapons" и "banned substance"), то он бросает packages. IllegalPackageException.
Если он находит посылку, состоящую из камней (содержит слово "stones"), то тревога прозвучит в виде
packages. StolenPackageException. Оба исключения вы должны объявить самостоятельно в виде непроверяемых исключений.
 */
public static class Inspector implements MailService {

    public Inspector() { }

    private void checkPkg (MailPackage mp) throws RuntimeException {

        String mpContent = mp.getContent().getContent();

        if (mpContent.contains(WEAPONS) || mpContent.contains(BANNED_SUBSTANCE)) {
            throw new IllegalPackageException();
        } else if (mpContent.contains("stones")) {
            throw new StolenPackageException();
        }
    }

    @Override
    public Sendable processMail(Sendable mail) throws RuntimeException {
        if (mail instanceof MailPackage) {
            checkPkg((MailPackage) mail);
        }
        return mail;
    }
}