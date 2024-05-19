### Перебор SSH паролей с помощью гидры
- https://te.legra.ph/Kak-vzlomat-parol-SSH-s-pomoshchyu-Hydra-06-20
- https://te.legra.ph/Brutim-SSH-udalennogo-servera-06-15
- https://telegra.ph/Kak-perehvatit-parol-SSH-Ataka-chelovek-poseredine-na-SSH-03-14

### Уязвимости прикладного уровня HTTP
- Cookies
- Same origin policy
JS code не имеет доступа к HTTP only cookie, (Эти куки доступны только на уровне HTTP запросов и не могут быть прочитамы JS кодом в браузере, так защищает от кражи сессий пользователя)
- SOP код выполняетмый на одном origin, не имеет доступа к другим origin

---
```
Injections (SQL, LDAP)
Cross site scripting (XSS) - executes some code in users browser
Cross site request forgery (CSRF) - use users credentials
Unseure sessions
```

