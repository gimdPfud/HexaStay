package com.sixthsense.hexastay.config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(memberDetailsService);
            provider.setPasswordEncoder(passwordEncoder());

            AuthenticationManager authManager = new ProviderManager(provider);

            http
                    .securityMatcher("/main/**", "/member/**", "/branch/**", "/center/**",
                            "/facility/**", "/faq/**", "/hotelroom/**", "/maechulroom/**",
                            "/notice/**", "/review/**", "/room/**", "/roommenu/**",
                            "/roomMenu/**",
                            "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/toss/**")
                    .authenticationManager(authManager)
                    .userDetailsService(memberDetailsService)
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

        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
