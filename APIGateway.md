- API gateway, по регистру понимает какой микросервис вызывать
- Спринг клауд может работать с любыми мкросервисами, на других яп, которые поддерживаются
- Через API gateway можно настроить аутентификацию и авторизацию
- интерфейсы это межпакетный контракт

- **Resilience** когда падают все сервисы
- **Fault tolerance** - 
- **Discovery service** - какие микросервисы подняты, на каких адресах, сколько реплик. Spring Eurika discovery service кэширует данные что позволяет работать приложению даже при падении (перенаправит по знающим путям, он просто не сможет добавить новые сервисы).