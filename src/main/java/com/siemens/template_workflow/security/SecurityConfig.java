package com.siemens.template_workflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // for simplicity; in production configure CSRF protection properly
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**", "/", "/index.html", "/static/**", "/css/**", "/js/**", "/app.js", "/styles.css").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().disable()
            .httpBasic().disable()
            .sessionManagement().maximumSessions(1);

        return http.build();
    }
}

