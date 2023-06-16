package mail.services;

import mail.packages.exceptions.IllegalPackageException;
import mail.packages.exceptions.StolenPackageException;
import mail.types.MailPackage;
import mail.types.Sendable;

import static mail.Keywords.*;

/*
4) Inspector – Инспектор, который следит за запрещенными и украденными посылками и
бьет тревогу в виде исключения, если была обнаружена подобная посылка. Если он заметил запрещенную посылку
с одним из запрещенных содержимым ("weapons" и "banned substance"), то он бросает packages. IllegalPackageException.
Если он находит посылку, состоящую из камней (содержит слово "stones"), то тревога прозвучит в виде
packages. StolenPackageException. Оба исключения вы должны объявить самостоятельно в виде непроверяемых исключений.
 */
public class Inspector implements MailService {

    public Inspector() { }

    private void checkPkg (MailPackage mp) throws RuntimeException {

        String mpContent = mp.getContent().getContent();

        if (mpContent.contains(WEAPONS) || mpContent.contains(BANNED_SUBSTANCE)) {
            throw new IllegalPackageException();
        } else if (mpContent.contains(STONES)) {
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
