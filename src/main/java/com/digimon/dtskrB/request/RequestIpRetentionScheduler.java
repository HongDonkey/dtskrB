package com.digimon.dtskrB.request;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestIpRetentionScheduler {

    private static final Logger log = LoggerFactory.getLogger(RequestIpRetentionScheduler.class);

    private final JdbcTemplate jdbcTemplate;
    private final Duration retentionPeriod;

    public RequestIpRetentionScheduler(
            JdbcTemplate jdbcTemplate,
            @Value("${app.request-board.ip-retention-period:30d}") Duration retentionPeriod) {
        if (retentionPeriod.isNegative() || retentionPeriod.isZero()) {
            throw new IllegalArgumentException("The request IP retention period must be positive.");
        }
        this.jdbcTemplate = jdbcTemplate;
        this.retentionPeriod = retentionPeriod;
    }

    @Scheduled(
            initialDelayString = "${app.request-board.ip-cleanup-initial-delay:1m}",
            fixedDelayString = "${app.request-board.ip-cleanup-interval:24h}")
    public void removeExpiredRequesterIps() {
        Timestamp cutoff = Timestamp.from(Instant.now().minus(retentionPeriod));
        int updated = jdbcTemplate.update("""
                UPDATE request_post
                SET requester_ip = NULL
                WHERE requester_ip IS NOT NULL AND created_at < ?
                """, cutoff);
        if (updated > 0) log.info("Expired requester IP addresses removed: count={}", updated);
    }
}
