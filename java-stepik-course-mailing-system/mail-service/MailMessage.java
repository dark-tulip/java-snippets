/**
 * Наследование с типом контента String
 */
public class MailMessage extends MailSender<String> {
    public MailMessage(String from, String to, String content) {
        super(from, to, content);
    }
}