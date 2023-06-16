/**
 * Реализация методов в базовом классе поможет избежать
 * идентичного дублирования кода в дочерних классах
 * @param <T> тип отправляемого контента
 */
public class MailSender<T> implements ISendable<T> {

    String from;
    String to;
    T content;

    public MailSender(String from, String to, T content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public T getContent() {
        return content;
    }
}
