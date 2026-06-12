package com.primaryenglish.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 靜態資源與公開頁面
                .requestMatchers("/", "/login", "/do-login", "/register", "/logout",
                                 "/do-login", "/do-register",
                                 "/vocabulary", "/quiz/**", "/reading/**",
                                 "/css/**", "/js/**", "/images/**", "/fonts/**",
                                 "/bootstrap.min.css", "/tabler-icons.min.css", "/css/**")
                .permitAll()
                // AI 對話 API 需登入（必須在 /api/** 之前）
                .requestMatchers("/api/ai-conversation/**")
                .authenticated()
                // 其他 API 公開
                .requestMatchers("/api/**")
                .permitAll()
                // 管理後台需登入
                .requestMatchers("/admin/**")
                .authenticated()
                // 個人資料與 AI 對話需登入
                .requestMatchers("/profile", "/ai-conversation")
                .authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }
}
