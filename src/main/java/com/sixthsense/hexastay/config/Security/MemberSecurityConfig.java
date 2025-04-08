package com.sixthsense.hexastay.config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

    @Configuration
    public class MemberSecurityConfig {

        private final CustomMemberDetailsService memberDetailsService;

        public MemberSecurityConfig(CustomMemberDetailsService memberDetailsService) {
            this.memberDetailsService = memberDetailsService;
        }

        @Bean(name = "memberSecurityFilterChain")
        @Order(2)
        public SecurityFilterChain memberFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/member/**")
                    .authorizeHttpRequests(authz -> authz
                            .anyRequest().permitAll()
                    )
                    .formLogin(form -> form
                            .loginPage("/member/login")
                            .defaultSuccessUrl("/member/main")
                            .failureUrl("/member/login?error")
                            .usernameParameter("memberEmail")
                            .passwordParameter("memberPassword")
                            .permitAll()
                    )
                    .userDetailsService(memberDetailsService)
                    .csrf().disable();

            return http.build();
        }
    }
