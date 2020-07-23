package io.floodplain.miroassignment.impl;

import io.floodplain.miroassignment.model.RateLimitResponse;
import io.floodplain.miroassignment.model.RateLimiter;
import io.github.bucket4j.*;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitImplementation implements RateLimiter {
    public static final long DEFAULT_REQUESTS_PER_MINUTE = 1000;

    private long maxRequestsPerminute = DEFAULT_REQUESTS_PER_MINUTE;
    private final Bucket bucket = Bucket4j.builder().addLimit(Bandwidth.simple(DEFAULT_REQUESTS_PER_MINUTE,Duration.ofMinutes(1))).build();


    public RateLimitImplementation() {
        // set initial to configure the bucket
        setMaxRequestsPerMinute(maxRequestsPerminute);
    }

    @Override
    public RateLimitResponse request(long numberOfTokens) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(numberOfTokens);
        boolean isOk = probe.isConsumed();
        var duration = Duration.ofNanos(probe.getNanosToWaitForRefill());
        var remaining = probe.getRemainingTokens();
        return new RateLimitResponse(isOk,maxRequestsPerminute,remaining,duration);
    }

    /**
     * @param refillRate The number of available tokens per minute, as well as the initial number.
     * (Possible 'leak' if the client is able to change the mrpm thus resetting the available count)
     */
    public void setMaxRequestsPerMinute(long refillRate) {
        maxRequestsPerminute = refillRate;
        long numberOfUndesiredTokens = bucket.getAvailableTokens() - refillRate;
        bucket.replaceConfiguration(Bucket4j.configurationBuilder()
                .addLimit(Bandwidth.classic(maxRequestsPerminute, Refill.intervally(maxRequestsPerminute, Duration.ofMinutes(1))))
                .build());

        if(numberOfUndesiredTokens > 0) {
            bucket.tryConsumeAsMuchAsPossible(numberOfUndesiredTokens);
        } else {
            // add missing tokens
            long deficientTokens = -numberOfUndesiredTokens;
            if(deficientTokens>0) {
                bucket.addTokens(deficientTokens);
            }
        }
    }
}
