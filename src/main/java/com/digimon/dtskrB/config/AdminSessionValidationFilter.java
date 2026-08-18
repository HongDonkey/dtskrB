package com.digimon.dtskrB.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminSessionValidationFilter extends OncePerRequestFilter {

    public static final String CREDENTIAL_VERSION_SESSION_KEY = "ADMIN_CREDENTIAL_VERSION";
    public static final String PASSWORD_CHANGE_REQUIRED_SESSION_KEY = "ADMIN_PASSWORD_CHANGE_REQUIRED";

    private final JdbcTemplate jdbcTemplate;

    public AdminSessionValidationFilter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/admin/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        List<AdminCredentialState> states = jdbcTemplate.query("""
                SELECT is_active, credential_version, password_change_required
                FROM admin_user
                WHERE login_id = ?
                """, (resultSet, rowNumber) -> new AdminCredentialState(
                        resultSet.getBoolean("is_active"),
                        resultSet.getLong("credential_version"),
                        resultSet.getBoolean("password_change_required")),
                authentication.getName());
        HttpSession session = request.getSession(false);
        Object sessionVersion = session == null ? null : session.getAttribute(CREDENTIAL_VERSION_SESSION_KEY);
        if (states.isEmpty() || !states.getFirst().active()
                || !(sessionVersion instanceof Number version)
                || version.longValue() != states.getFirst().credentialVersion()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "SESSION_EXPIRED", "Administrator session has expired.");
            return;
        }

        boolean passwordChangeRequired = states.getFirst().passwordChangeRequired();
        session.setAttribute(PASSWORD_CHANGE_REQUIRED_SESSION_KEY, passwordChangeRequired);
        if (passwordChangeRequired && !isPasswordChangeEndpoint(request.getRequestURI())) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                    "PASSWORD_CHANGE_REQUIRED", "The administrator password must be changed first.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPasswordChangeEndpoint(String uri) {
        return uri.equals("/api/admin/auth/password")
                || uri.equals("/api/admin/auth/logout")
                || uri.equals("/api/admin/auth/status")
                || uri.equals("/api/admin/auth/csrf");
    }

    private void writeJson(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }

    private record AdminCredentialState(boolean active, long credentialVersion, boolean passwordChangeRequired) { }
}
