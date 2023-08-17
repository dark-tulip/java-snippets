- из коробки предоставляет функционал аутентификации при старте веб приложения
- вход на основе ролей не гибок (use Authority, with given permissions)
- Basic Auth связка `логина:пароля` передаваемая в header-ах запроса, закодированная в Base64
- если с SecurityConfiguration убрать basicAuth()  basic auth не будет работать
```Java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(withDefaults())
//                .authorizeHttpRequests(
//                        (authorizeHttpRequests) -> authorizeHttpRequests
//                                .requestMatchers("/index.html").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.PERMISSION_READ.getPermission())
//                                .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.PERMISSION_WRITE.getPermission())
//                                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.PERMISSION_WRITE.getPermission())
//
//                )
                .formLogin(withDefaults());
        return http.build();
    }
```
- при успешном логине создается сессия для пользователя (при каждом запросе передается JSESSIONID=4B1BFFE699BF33C51220DFDD8EBF2E5D)
- после перезапуска приложения все сессии истекают

`SpringSecurityHolder` хранит весь secure-ный контекст (кто куда имеет доступ)
- user Authentication который хранит в себе
- - authorities
  - username
  - isValid


### Local postgres in docker
```
docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres
```
### JWT
- iat - issued at time (время создания токена)
- c помощью аннотации `@Value("${jwt.secret}")` можно достать значение параметра из `application.properties`
