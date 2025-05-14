package com.sixthsense.hexastay.config.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                // 세션 관리 설정
                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션을 필요한 경우에만 생성
                        //.maximumSessions(3)  // 한 사용자당 최대 세션 수 설정
                        //.maxSessionsPreventsLogin(true)  // 최대 세션 수 초과 시 로그인 방지
               );
        http
                .securityMatcher("/admin/**",
                        "/facility/**", "/faq/**", "/hotelroom/**", "/maechulroom/**",
                        "/notice/**", "/review/**", "/room/**", "/company/**",
                        "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/settle",
                        "/toss/**", "/js/**", "/roomlist/**", "/member-insertroom/**",
                        "/success", "/fail", "/payment", "/settle/**", "/erd/**",
                        "/register-hotelroom/**", "/salaries/**", "/survey/**", "/surveresult/**")

                .authenticationManager(authManager)
                .userDetailsService(adminDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/encrypt-admin-password", "/admin/create-admin",
                                "/admin/updateidentity", "/admin/passwordcode", "/admin/resetpassword").permitAll()
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

        };
    }

}
