package com.sixthsense.hexastay.config.Security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminSecurityConfig {

    private final CustomAdminDetailsService adminDetailsService;
    private static final Logger log = LoggerFactory.getLogger(AdminSecurityConfig.class);

    public AdminSecurityConfig(CustomAdminDetailsService adminDetailsService) {
        this.adminDetailsService = adminDetailsService;
    }

    @Bean(name = "adminSecurityFilterChain")
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        AuthenticationManager authManager = new ProviderManager(provider);

        http
                .securityMatcher("/admin/**",
                        "/facility/**", "/faq/**", "/hotelroom/**", "/maechulroom/**",
                        "/notice/**", "/review/**", "/room/**", "/company/**",
                        "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/settle",
                        "/toss/**", "/js/**", "/roomlist/**", "/member-insertroom/**",
                        "/success", "/fail", "/payment", "/settle/**", "/erd/**",
                        "/register-hotelroom/**", "/salaries/**")

                .authenticationManager(authManager)
                .userDetailsService(adminDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/encrypt-admin-password", "/admin/create-admin").permitAll()
                        .requestMatchers(("/ws-order-alert/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .defaultSuccessUrl("/admin/main")
                        .failureUrl("/admin/login?error")
                        .usernameParameter("adminEmail")
                        .passwordParameter("adminPassword")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendRedirect("/admin/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                log.info("비밀번호 검증 시작");
                log.info("입력된 비밀번호: {}", rawPassword);
                log.info("DB의 비밀번호: {}", encodedPassword);
                boolean matches = super.matches(rawPassword, encodedPassword);
                log.info("비밀번호 일치 여부: {}", matches);
                return matches;
            }
        };
    }

}
