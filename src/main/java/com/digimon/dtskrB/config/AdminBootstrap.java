package com.digimon.dtskrB.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final String loginId;
    private final String password;
    private final String displayName;

    public AdminBootstrap(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.bootstrap.login-id:}") String loginId,
            @Value("${app.admin.bootstrap.password:}") String password,
            @Value("${app.admin.bootstrap.display-name:Administrator}") String displayName) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.loginId = loginId;
        this.password = password;
        this.displayName = displayName;
    }

    @Override
    public void run(ApplicationArguments arguments) {
        Integer activeAdminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_user WHERE role = 'ADMIN' AND is_active = 1", Integer.class);
        if (activeAdminCount != null && activeAdminCount > 0) return;

        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(password)) {
            log.error("No active administrator exists. Set ADMIN_BOOTSTRAP_LOGIN_ID and "
                    + "ADMIN_BOOTSTRAP_PASSWORD, then restart once to initialize an administrator.");
            return;
        }
        if (loginId.trim().length() < 3 || loginId.trim().length() > 50) {
            throw new IllegalStateException("ADMIN_BOOTSTRAP_LOGIN_ID must be 3-50 characters.");
        }
        if (!isStrongPassword(password)) {
            throw new IllegalStateException("ADMIN_BOOTSTRAP_PASSWORD must be 8-72 characters and include "
                    + "a letter, number, and symbol.");
        }

        jdbcTemplate.update("""
                INSERT INTO admin_user
                    (login_id, password_hash, display_name, role, is_active,
                     failed_login_attempts, locked_until, password_change_required, credential_version)
                VALUES (?, ?, ?, 'ADMIN', 1, 0, NULL, 1, 1)
                ON DUPLICATE KEY UPDATE
                    password_hash = VALUES(password_hash),
                    display_name = VALUES(display_name),
                    role = 'ADMIN',
                    is_active = 1,
                    failed_login_attempts = 0,
                    locked_until = NULL,
                    password_change_required = 1,
                    credential_version = credential_version + 1
                """, loginId.trim(), passwordEncoder.encode(password), normalizedDisplayName());
        log.warn("An administrator was initialized from the deployment bootstrap secret. "
                + "Remove ADMIN_BOOTSTRAP_PASSWORD and change the password at first login.");
    }

    private String normalizedDisplayName() {
        if (!StringUtils.hasText(displayName)) return "Administrator";
        String normalized = displayName.trim();
        return normalized.length() <= 50 ? normalized : normalized.substring(0, 50);
    }

    private boolean isStrongPassword(String value) {
        return value.length() >= 8 && value.length() <= 72
                && value.matches(".*[A-Za-z].*")
                && value.matches(".*[0-9].*")
                && value.matches(".*[^A-Za-z0-9].*");
    }
}
