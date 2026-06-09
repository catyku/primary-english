package com.primaryenglish.config;

import com.primaryenglish.entity.User;
import com.primaryenglish.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    /**
     * 自定義攔截器：從 Session 中讀取使用者，設到 Spring Security Context
     */
    @Bean
    public HandlerInterceptor sessionUserInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    String username = (String) session.getAttribute("USER_NAME");
                    if (username != null) {
                        // 設置一個簡單的認證標記到 Security Context
                        // 實際上我們用 Session 就够了，這裡只是為了 Thymeleaf sec: 標籤能工作
                        org.springframework.security.core.Authentication auth =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                username, null,
                                java.util.Collections.singletonList(
                                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                                )
                            );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionUserInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/vocabulary",
                                 "/quiz/**", "/api/**",
                                 "/css/**", "/js/**", "/images/**", "/fonts/**",
                                 "/login", "/register", "/do-login", "/do-register",
                                 "/profile", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())  // 禁用 Spring Security 預設表單登入
            .logout(logout -> logout.disable()) // 禁用預設登出
            .csrf(csrf -> csrf.disable())        // 關閉 CSRF（簡易模式）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }
}
