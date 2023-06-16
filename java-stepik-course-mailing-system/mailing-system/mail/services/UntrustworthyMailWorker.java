package mail.services;

import mail.types.Sendable;

/*
 1) UntrustworthyMailWorker – класс, моделирующий ненадежного работника почты,
 который вместо того, чтобы передать почтовый объект непосредственно в сервис почты,
 последовательно передает этот объект набору третьих лиц, а затем, в конце концов,
 передает получившийся объект непосредственно экземпляру mail_services. RealMailService.

 У mail_services. UntrustworthyMailWorker должен быть конструктор от массива mail_services. MailService
 (результат вызова processMail первого элемента массива передается на вход processMail второго элемента, и т. д.)
 и метод getRealMailService, который возвращает ссылку на внутренний экземпляр mail_services. RealMailService.
 */
public class UntrustworthyMailWorker implements MailService {

    private static MailService[] workers;
    private static final RealMailService rms = new RealMailService();

    public UntrustworthyMailWorker(MailService[] ms) {
        workers = ms;
    }

    public MailService getRealMailService() {
        return rms;
    }

    @Override
    public Sendable processMail(Sendable mail) throws RuntimeException {
        for (MailService m : workers) {
            mail = m.processMail(mail);
        }
        return rms.processMail(mail);
    }
}
