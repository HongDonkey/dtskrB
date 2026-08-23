package com.digimon.dtskrB.digimon.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorStatisticsService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final JdbcTemplate jdbcTemplate;
    private final byte[] hashSecret;

    public VisitorStatisticsService(
            JdbcTemplate jdbcTemplate,
            @Value("${app.visitor.hash-secret}") String hashSecret) {
        this.jdbcTemplate = jdbcTemplate;
        this.hashSecret = hashSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public void recordVisit(String remoteAddress) {
        LocalDate today = LocalDate.now();
        String visitorHash = createDailyHash(today, normalizeAddress(remoteAddress));
        int inserted = jdbcTemplate.update(
                "INSERT IGNORE INTO daily_visitor (visit_date, visitor_hash) VALUES (?, ?)",
                today, visitorHash);
        if (inserted == 1) {
            jdbcTemplate.update("""
                    UPDATE visitor_statistics
                    SET total_visitors = total_visitors + 1
                    WHERE id = 1
                    """);
        }
        jdbcTemplate.update("DELETE FROM daily_visitor WHERE visit_date < ?", today.minusDays(2));
    }

    public long getTodayVisitorCount() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM daily_visitor WHERE visit_date = CURRENT_DATE", Long.class);
        return count == null ? 0L : count;
    }

    public long getTotalVisitorCount() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT total_visitors FROM visitor_statistics WHERE id = 1", Long.class);
        return count == null ? 0L : count;
    }

    private String createDailyHash(LocalDate date, String remoteAddress) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(hashSecret, HMAC_ALGORITHM));
            byte[] digest = mac.doFinal((date + "|" + remoteAddress).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to create the daily visitor identifier.", exception);
        }
    }

    private static String normalizeAddress(String remoteAddress) {
        if (remoteAddress == null || remoteAddress.isBlank()) return "unknown";
        return remoteAddress.trim().toLowerCase();
    }
}
