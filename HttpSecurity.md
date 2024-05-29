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
