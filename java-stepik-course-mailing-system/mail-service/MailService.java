import java.util.*;
import java.util.function.Consumer;

public class MailService<T> implements Consumer<ISendable<T>>  {

    /**
     * Это поле - хранилище сообщений, созданное классом с переопределенным get методом
     */
    private Map<String, List<T>> mailBox = new HashMap<String, List<T>>() {
        /**
         * Переопределяем get, используя getOrDefault из HashMap
         * при отсутствии ключа - возвращаем пустую коллекцию
         */
        @Override
        public List<T> get(Object key) {
            return super.getOrDefault(key, new ArrayList<>());
        }
    };

    public Map<String, List<T>> getMailBox() {
        return mailBox;
    }

    /**
     * forEach, forEachOrdered ожидает реализацию интерфейса Consumer-а
     * Потребитель потребляет контент - поэтому дженерик типа
     * @param iSendable
     */
    @Override
    public void accept(ISendable<T> iSendable) {
        List<T> contents = mailBox.get(iSendable.getTo());
        contents.add(iSendable.getContent());
        mailBox.put(iSendable.getTo(), contents);
    }
}