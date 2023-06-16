package mail.services;

import mail.types.MailMessage;
import mail.types.Sendable;

import java.util.logging.Level;
import java.util.logging.Logger;

import static mail.Keywords.TARGET_USER;

/*
2) Spy – шпион, который логгирует о всей почтовой переписке, которая проходит через его руки.
 Объект конструируется от экземпляра Logger, с помощью которого шпион будет сообщать о всех действиях.
 Он следит только за объектами класса mail_types.MailMessage и пишет в логгер следующие сообщения
 (в выражениях нужно заменить части в фигурных скобках на значения полей почты):
2.1) Если в качестве отправителя или получателя указан "Austin Powers", то нужно написать в лог
 сообщение с уровнем WARN: Detected target mail correspondence: from {from} to {to} "{message}"
2.2) Иначе, необходимо написать в лог сообщение с уровнем INFO: Usual correspondence: from {from} to {to}
 */
public class Spy implements MailService {

    private static Logger logger = Logger.getLogger(MailMessage.class.getName());

    public Spy(Logger logger) {
        Spy.logger = logger;
    }

    @Override
    public Sendable processMail(Sendable mail) {
        if (mail instanceof MailMessage) {
            MailMessage msg = (MailMessage) mail;
            if (msg.getFrom().equalsIgnoreCase(TARGET_USER) || msg.getTo().equalsIgnoreCase(TARGET_USER)) {
                logger.log(Level.WARNING, "Detected target mail correspondepnce: from {0} to {1} \"{2}\"", new Object[] {msg.getFrom(), msg.getTo(), msg.getMessage()});
            } else {
                logger.log(Level.INFO, "Usual correspondence: from {0} to {1}", new Object[] {msg.getFrom(), msg.getTo()});
            }
        }
        return mail;
    }
}
