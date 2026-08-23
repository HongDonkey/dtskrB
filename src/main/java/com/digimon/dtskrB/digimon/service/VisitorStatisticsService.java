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

    public void recordVisit(String remoteAddress) {
        LocalDate today = LocalDate.now();
        String visitorHash = createDailyHash(today, normalizeAddress(remoteAddress));
        jdbcTemplate.update("""
                INSERT INTO daily_visitor (visit_date, visitor_hash)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE last_visited_at = CURRENT_TIMESTAMP
                """, today, visitorHash);
        jdbcTemplate.update("DELETE FROM daily_visitor WHERE visit_date < ?", today.minusDays(2));
    }

    public long getTodayVisitorCount() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM daily_visitor WHERE visit_date = CURRENT_DATE", Long.class);
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
