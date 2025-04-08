package com.sixthsense.hexastay.config.Security;

import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
@EnableWebSecurity
@RequiredArgsConstructor
@Order(2)
public class MemberSecurityConfig {

    private final MemberRepository memberRepository;

    @Bean
    public UserDetailsService memberDetailsService() {
        return new CustomMemberDetailsService(memberRepository);
    }

    @Bean
    public AuthenticationManager memberAuthenticationManager(
            UserDetailsService memberDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean(name = "memberSecurityFilterChain")
    public SecurityFilterChain memberSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/member/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**" ).permitAll()
//                        .requestMatchers("/main").hasAnyRole("wait", "active", "admin")
//                        .requestMatchers("/memberList").hasRole("admin")
//                        .requestMatchers("/**").hasAnyRole("active","admin")

                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/member/login")
                        .usernameParameter("memberEmail")
                        .passwordParameter("memberPassword")
                        .defaultSuccessUrl("/member/main", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/member/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/member/main");
                        })
                )
                .csrf(csrf -> csrf.disable()); // CSRF

        return http.build();
    }



}