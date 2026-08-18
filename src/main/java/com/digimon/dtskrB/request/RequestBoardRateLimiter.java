package com.digimon.dtskrB.request;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestBoardRateLimiter {

    private static final int MAX_TRACKED_KEYS = 10_000;

    private final Map<String, ArrayDeque<Instant>> attempts = new ConcurrentHashMap<>();
    private final AtomicLong operationCount = new AtomicLong();
    private final int createLimit;
    private final Duration createWindow;
    private final int lookupLimit;
    private final Duration lookupWindow;

    public RequestBoardRateLimiter(
            @Value("${app.request-board.rate-limit.create.limit:3}") int createLimit,
            @Value("${app.request-board.rate-limit.create.window:10m}") Duration createWindow,
            @Value("${app.request-board.rate-limit.lookup.limit:10}") int lookupLimit,
            @Value("${app.request-board.rate-limit.lookup.window:5m}") Duration lookupWindow) {
        this.createLimit = requirePositive(createLimit, "create limit");
        this.createWindow = requirePositive(createWindow, "create window");
        this.lookupLimit = requirePositive(lookupLimit, "lookup limit");
        this.lookupWindow = requirePositive(lookupWindow, "lookup window");
    }

    public void checkCreate(String remoteAddress) {
        consume("create:ip:" + normalizeAddress(remoteAddress), createLimit, createWindow);
    }

    public void checkLookup(String remoteAddress, String requesterName) {
        String address = normalizeAddress(remoteAddress);
        consume("lookup:ip:" + address, lookupLimit, lookupWindow);
        consume("lookup:identity:" + address + ':' + sha256(normalizeName(requesterName)),
                lookupLimit, lookupWindow);
    }

    private void consume(String key, int limit, Duration window) {
        Instant now = Instant.now();
        ArrayDeque<Instant> bucket = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (bucket) {
            Instant cutoff = now.minus(window);
            while (!bucket.isEmpty() && !bucket.peekFirst().isAfter(cutoff)) bucket.removeFirst();
            if (bucket.size() >= limit) {
                long retryAfter = Math.max(1,
                        Duration.between(now, bucket.peekFirst().plus(window)).toSeconds() + 1);
                throw new RateLimitExceededException(retryAfter);
            }
            bucket.addLast(now);
        }
        if (operationCount.incrementAndGet() % 100 == 0) cleanup(now);
    }

    private void cleanup(Instant now) {
        if (attempts.size() < MAX_TRACKED_KEYS) return;
        Instant staleBefore = now.minus(Duration.ofDays(1));
        attempts.entrySet().removeIf(entry -> {
            ArrayDeque<Instant> bucket = entry.getValue();
            synchronized (bucket) {
                return bucket.isEmpty() || bucket.peekLast().isBefore(staleBefore);
            }
        });
    }

    private static String normalizeAddress(String remoteAddress) {
        if (remoteAddress == null || remoteAddress.isBlank()) return "unknown";
        String normalized = remoteAddress.trim().toLowerCase();
        return normalized.length() <= 64 ? normalized : sha256(normalized);
    }

    private static String normalizeName(String requesterName) {
        return requesterName == null ? "" : requesterName.trim().toLowerCase();
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }

    private static int requirePositive(int value, String name) {
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    private static Duration requirePositive(Duration value, String name) {
        if (value == null || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive.");
        }
        return value;
    }
}
