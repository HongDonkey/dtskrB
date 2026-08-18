package com.digimon.dtskrB.digimon.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digimon.dtskrB.config.AdminSessionValidationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final JdbcTemplate jdbcTemplate;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthController(
            JdbcTemplate jdbcTemplate,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken(), "headerName", csrfToken.getHeaderName());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        clearExpiredLock(request.loginId());
        if (isLocked(request.loginId())) {
            log.warn("Admin login rejected because the account is locked: loginId={}, remoteAddress={}",
                    sanitizeForLog(request.loginId()), sanitizeForLog(servletRequest.getRemoteAddr()));
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("authenticated", false,
                            "message", "Too many failed attempts. Try again in 15 minutes."));
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.loginId(), request.password()));
        } catch (AuthenticationException exception) {
            recordFailedAttempt(request.loginId());
            LoginAuditState auditState = findLoginAuditState(request.loginId());
            log.warn("Admin login failed: loginId={}, remoteAddress={}, reason={}, failedAttempts={}, lockedUntil={}",
                    sanitizeForLog(request.loginId()), sanitizeForLog(servletRequest.getRemoteAddr()),
                    exception.getClass().getSimpleName(), auditState.failedAttempts(), auditState.lockedUntil());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false, "message", "Invalid credentials."));
        }

        HttpSession existingSession = servletRequest.getSession(false);
        if (existingSession != null) servletRequest.changeSessionId();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);

        AdminCredentialState credentialState = jdbcTemplate.query("""
                SELECT credential_version, password_change_required
                FROM admin_user
                WHERE login_id = ? AND is_active = 1
                """, (resultSet, rowNumber) -> new AdminCredentialState(
                        resultSet.getLong("credential_version"),
                        resultSet.getBoolean("password_change_required")), request.loginId())
                .stream().findFirst().orElseThrow();
        HttpSession authenticatedSession = servletRequest.getSession();
        authenticatedSession.setAttribute(AdminSessionValidationFilter.CREDENTIAL_VERSION_SESSION_KEY,
                credentialState.credentialVersion());
        authenticatedSession.setAttribute(AdminSessionValidationFilter.PASSWORD_CHANGE_REQUIRED_SESSION_KEY,
                credentialState.passwordChangeRequired());

        jdbcTemplate.update("""
                UPDATE admin_user
                SET failed_login_attempts = 0, locked_until = NULL, last_login_at = CURRENT_TIMESTAMP
                WHERE login_id = ?
                """, request.loginId());
        log.info("Admin login succeeded: loginId={}, remoteAddress={}",
                sanitizeForLog(request.loginId()), sanitizeForLog(servletRequest.getRemoteAddr()));
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "passwordChangeRequired", credentialState.passwordChangeRequired()));
    }

    @GetMapping("/status")
    public Map<String, Object> status(Authentication authentication, HttpSession session) {
        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean passwordChangeRequired = authenticated && Boolean.TRUE.equals(
                session.getAttribute(AdminSessionValidationFilter.PASSWORD_CHANGE_REQUIRED_SESSION_KEY));
        return Map.of(
                "authenticated", authenticated,
                "passwordChangeRequired", passwordChangeRequired);
    }

    @PostMapping("/logout")
    public Map<String, Boolean> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        String loginId = authentication == null ? "anonymous" : authentication.getName();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        log.info("Admin logged out: loginId={}, remoteAddress={}",
                sanitizeForLog(loginId), sanitizeForLog(request.getRemoteAddr()));
        return Map.of("authenticated", false);
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        if (!isStrongPassword(request.newPassword())) {
            return ResponseEntity.badRequest().body(Map.of("changed", false,
                    "message", "The new password must be 8-72 characters and include a letter, number, and symbol."));
        }

        List<String> passwordHashes = jdbcTemplate.query(
                "SELECT password_hash FROM admin_user WHERE login_id = ? AND is_active = 1",
                (resultSet, rowNumber) -> resultSet.getString("password_hash"), authentication.getName());
        if (passwordHashes.isEmpty() || !passwordEncoder.matches(request.currentPassword(), passwordHashes.getFirst())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("changed", false, "message", "The current password is incorrect."));
        }

        jdbcTemplate.update("""
                UPDATE admin_user
                SET password_hash = ?, password_change_required = 0,
                    credential_version = credential_version + 1,
                    failed_login_attempts = 0, locked_until = NULL
                WHERE login_id = ?
                """,
                passwordEncoder.encode(request.newPassword()), authentication.getName());
        log.info("Admin password changed and existing sessions invalidated: loginId={}, remoteAddress={}",
                sanitizeForLog(authentication.getName()), sanitizeForLog(servletRequest.getRemoteAddr()));
        new SecurityContextLogoutHandler().logout(servletRequest, servletResponse, authentication);
        return ResponseEntity.ok(Map.of("changed", true, "authenticated", false));
    }

    private void clearExpiredLock(String loginId) {
        jdbcTemplate.update("""
                UPDATE admin_user
                SET failed_login_attempts = 0, locked_until = NULL
                WHERE login_id = ? AND locked_until IS NOT NULL AND locked_until <= CURRENT_TIMESTAMP
                """, loginId);
    }

    private boolean isLocked(String loginId) {
        return jdbcTemplate.query("""
                SELECT locked_until FROM admin_user
                WHERE login_id = ? AND is_active = 1 AND locked_until > CURRENT_TIMESTAMP
                """, (resultSet, rowNumber) -> resultSet.getObject("locked_until", LocalDateTime.class), loginId)
                .stream().findFirst().isPresent();
    }

    private void recordFailedAttempt(String loginId) {
        jdbcTemplate.update("""
                UPDATE admin_user
                SET failed_login_attempts = failed_login_attempts + 1,
                    locked_until = CASE
                        WHEN failed_login_attempts + 1 >= ?
                        THEN DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? MINUTE)
                        ELSE locked_until
                    END
                WHERE login_id = ? AND is_active = 1
                """, MAX_FAILED_ATTEMPTS, LOCK_MINUTES, loginId);
    }

    private LoginAuditState findLoginAuditState(String loginId) {
        return jdbcTemplate.query("""
                SELECT failed_login_attempts, locked_until
                FROM admin_user
                WHERE login_id = ?
                """, (resultSet, rowNumber) -> new LoginAuditState(
                        resultSet.getInt("failed_login_attempts"),
                        resultSet.getObject("locked_until", LocalDateTime.class)), loginId)
                .stream().findFirst().orElse(new LoginAuditState(0, null));
    }

    private String sanitizeForLog(String value) {
        if (value == null) return "unknown";
        String sanitized = value.replace('\r', '_').replace('\n', '_').replace('\t', '_').trim();
        return sanitized.length() <= 64 ? sanitized : sanitized.substring(0, 64);
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && password.length() <= 72
                && password.matches(".*[A-Za-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    public record LoginRequest(@NotBlank String loginId, @NotBlank String password) {
    }

    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {
    }

    private record AdminCredentialState(long credentialVersion, boolean passwordChangeRequired) { }
    private record LoginAuditState(int failedAttempts, LocalDateTime lockedUntil) { }

}
