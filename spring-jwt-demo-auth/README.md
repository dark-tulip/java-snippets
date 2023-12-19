# Авторизация пользователя в спринге

### Регистрация пользователя
```bash
curl --location 'http://localhost:8080/api/auth/signup' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "user",
    "email": "user@mail.ru",
    "password": "pwd",
    "role": ["ROLE_USER"]
}'
```


![img.png](img/img.png)


### Get auth (Bearer) jwt token

```bash
curl --location 'http://localhost:8080/api/auth/signin' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user",
    "password": "pwd"
}'
```

![img_1.png](img/img_1.png)



### Call endpoint with credentials

```bash
curl --location 'http://localhost:8080/api/test/admin' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzAyOTc4MzM1LCJleHAiOjE3MDI5Nzg5MzV9.MDFidTqVuRX7emHS08fMKNPiB_zy7eqxYHoX9PST4CLXubN8YsltSoiHYiCBh5XE21c0EXOxcfX5f-iFOXMe1Q'
```

![img_2.png](img/img_2.png)


# JWT токены 
- состоят из трех частей
- записаны в HTTP заголовок `Authorization: Bearer`
- первые две части открытый payload в формате base64 encoded
- последняя - сигнатура
- нельзя пихать в jwt весь список ролей - от этого увеличивается длина токена
- максимальный размер HTTP заголовка 8кб

### С чем сравнивают?
- jwt часто сравнивают с SAML - security markup language tokens and SWT - simple web tokens
- SWT - is symmetrically signed algorithm
- быстрый маппинг в JSON объекты (удобно как со стороны сервера, так и со стороны клиента)

### Что такое claims?
- кусочек информации о субъекте в виде пары ключ-значение
- есть три вида
#### 1) registered claim names
- iss (Issuer claim) - principal that issued the JWT - кто создал выдал
- sub (Subject claim) - какому субъекту
- aud (Audience claim) 
- exp (Expiration time claim)
- nbf (Not Before claim)
- iat (Issued At claim) - был создан
- jti (JWT ID claim)
#### 2) public claim names
#### 3) private claim names