/**
 * Наследование с типом контента Integer от базового отправителя
 */
public class Salary extends MailSender<Integer> {
    public Salary(String from, String to, Integer content) {
        super(from, to, content);
    }
}