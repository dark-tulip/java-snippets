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
http://localhost:8080/api/v1/developers/1
