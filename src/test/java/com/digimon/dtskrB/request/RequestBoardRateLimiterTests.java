package com.digimon.dtskrB.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class RequestBoardRateLimiterTests {

    @Test
    void createRequestsAreLimitedPerAddress() {
        RequestBoardRateLimiter limiter = new RequestBoardRateLimiter(2, Duration.ofMinutes(10),
                10, Duration.ofMinutes(5));

        assertDoesNotThrow(() -> limiter.checkCreate("192.0.2.10"));
        assertDoesNotThrow(() -> limiter.checkCreate("192.0.2.10"));
        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class,
                () -> limiter.checkCreate("192.0.2.10"));

        assertTrue(exception.getRetryAfterSeconds() > 0);
        assertDoesNotThrow(() -> limiter.checkCreate("192.0.2.11"));
    }

    @Test
    void lookupsAreLimitedWithoutStoringTheRequesterNameAsAKey() {
        RequestBoardRateLimiter limiter = new RequestBoardRateLimiter(5, Duration.ofMinutes(10),
                2, Duration.ofMinutes(5));

        assertDoesNotThrow(() -> limiter.checkLookup("2001:db8::1", "작성자"));
        assertDoesNotThrow(() -> limiter.checkLookup("2001:db8::1", "작성자"));
        assertThrows(RateLimitExceededException.class,
                () -> limiter.checkLookup("2001:db8::1", "작성자"));
    }
}
