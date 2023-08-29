package ru.pufr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)                      // для того чтобы доступ раздавать прям в контроллерах GetMapping, PostMapping и т.д.
    public class SecurityConfiguration {

        private final UserDetailsService userDetailsService;


        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests((authz) -> authz
                            .anyRequest().authenticated()
                    )
                    .httpBasic(withDefaults());
            return http.build();
        }

        @Autowired
        public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

/*
        // авторизация по ролям
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/").permitAll()                                     // или здесь, или прям в анотации контроллеров
                    // .antMatchers("/blog").permitAll()
                    // .antMatchers("/blog/add").hasRole(Role.ADMIN.name())
                    //.anyRequest()                                                     // если убрать коменты, то запрос на ввод пароля будет на каждом переходе
                    //.authenticated()                                                  // если убрать коменты, то запрос на ввод пароля будет на каждом переходе
                    .and()
                    //.httpBasic();                                                     // эта кодировка нужна для авторизации по ролям.
                    .formLogin()
                    .loginPage("/auth/login").permitAll()
                    .defaultSuccessUrl("/auth/success")
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/auth/login");

        }
*/

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        @Bean
        protected PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(12);
        }

        @Bean
        protected DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
            daoAuthenticationProvider.setUserDetailsService(userDetailsService);
            return daoAuthenticationProvider;
        }
}
