package com.primaryenglish.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定義 Filter：從 Session 還原 Spring Security Context。
 * 因為 UserController.doLogin() 手動將 Authentication 寫入 Session，
 * 但 Spring Security 預設不會自動讀取，需要此 Filter 在每次請求時還原。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SessionSecurityContextFilter implements Filter {

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                Object contextFromSession = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
                if (contextFromSession instanceof SecurityContext) {
                    SecurityContextHolder.setContext((SecurityContext) contextFromSession);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
