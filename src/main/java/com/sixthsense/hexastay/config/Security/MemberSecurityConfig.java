package com.sixthsense.hexastay.config.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                // 세션 관리 설정
                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션을 필요한 경우에만 생성
                        //.maximumSessions(3)  // 한 사용자당 최대 세션 수 설정
                        //.maxSessionsPreventsLogin(true)  // 최대 세션 수 초과 시 로그인 방지
                );
        http
            .securityMatcher("/main/**", "/member/**", "/branch/**", "/center/**",
                    "/facility/**", "/faq/**", "/hotelroom/**", "/maechulroom/**",
                    "/notice/**", "/review/**", "/room/**", "/roommenu/**",
                    "/roomMenu/**","/qr/**,","/fs/**",
                    "/sample/**", "/sidebar/**", "/store/**", "/layouts/**", "/toss/**,",
                    "/ws-order-alert/**", "/cart/**", "/roomcare/**", "/api/reorder/**, ", "/api/**")
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
