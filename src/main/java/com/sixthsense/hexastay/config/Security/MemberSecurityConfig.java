package com.sixthsense.hexastay.config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class MemberSecurityConfig {

    private final CustomMemberDetailsService memberDetailsService;

    public MemberSecurityConfig(CustomMemberDetailsService memberDetailsService) {
        this.memberDetailsService = memberDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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
                    "/roomMenu/**","/qr/**,","/fs/**",
                    "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/toss/**,",
                    "/ws-order-alert/**", "/cart/**", "/roomcare/**", "/api/reorder/**, ",
                    "/api/**", "/adminreset")
            .authenticationManager(authManager)
            .userDetailsService(memberDetailsService)
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/guest-survey/**").permitAll()
                .anyRequest().permitAll()
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf().disable();

        return http.build();
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
