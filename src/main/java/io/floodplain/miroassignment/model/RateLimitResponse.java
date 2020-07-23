package io.floodplain.miroassignment.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Note: IntelliJ complains here, known bug,  should be fixed in the next update
 * @param success success, true if we are allowed to proceed
 * @param rateLimit the number of requests we can do in a minute
 * @param available the remaining request count for this minute
 * @param duration the remaining time until refill
 */
public record RateLimitResponse(
        boolean success,
        long rateLimit,
        long available,
        Duration untilNextReset) {
}
