package ru.job4j.cinema.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var uri = request.getRequestURI();

        if (isAlwaysPermitted(uri)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        var userLoggedIn = session != null && session.getAttribute("user") != null;

        if (isProtectedResource(uri) && !userLoggedIn) {
            var loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAlwaysPermitted(String uri) {
        return uri.startsWith("/users/register")
                || uri.startsWith("/users/login")
                || uri.startsWith("/files")
                || uri.startsWith("/css")
                || uri.startsWith("/js")
                || uri.startsWith("/images")
                || uri.equals("/")
                || uri.equals("/index");
    }

    private boolean isProtectedResource(String uri) {
        return uri.startsWith("/tickets/buy") || uri.startsWith("/tickets/mine");
    }
}