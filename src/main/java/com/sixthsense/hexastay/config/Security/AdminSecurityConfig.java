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

    @Configuration

    public class AdminSecurityConfig {

        private final CustomAdminDetailsService adminDetailsService;

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
                            "/notice/**", "/review/**", "/room/**",
                            "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/settle")

                    .authenticationManager(authManager)
                    .userDetailsService(adminDetailsService)
                    .authorizeHttpRequests(authz -> authz
//                            .requestMatchers("/memberList").hasRole("admin")
//                            .requestMatchers("/**").hasAnyRole("active","admin")
                            .anyRequest().permitAll()
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
                    .exceptionHandling(exception -> exception
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.sendRedirect("/admin/main");
                            })
                    )
                      .csrf().disable();

            return http.build();
        }


        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

}
