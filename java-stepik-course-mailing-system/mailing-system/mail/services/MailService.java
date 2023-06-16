package mail.services;

import mail.types.Sendable;

/*
Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
*/

public interface MailService {
    Sendable processMail(Sendable mail);
}

