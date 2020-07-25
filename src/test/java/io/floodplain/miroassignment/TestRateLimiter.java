package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.RateLimitResponse;
import io.floodplain.miroassignment.model.RateLimiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class TestRateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(TestRateLimiter.class);

    @Autowired
    private RateLimiter rateLimiter;

    @Test
    public void testSmallRateLimit() {
        rateLimiter.setMaxRequestsPerMinute(1);
        RateLimitResponse first = rateLimiter.request(1);
        logger.info("First response: {}",first);
        Assertions.assertTrue(first.success());
        RateLimitResponse second = rateLimiter.request(1);
        Assertions.assertFalse(second.success());
    }

    @Test
    public void testIncreaseRate() {
        // start small
        rateLimiter.setMaxRequestsPerMinute(1);
        RateLimitResponse first = rateLimiter.request(1);
        logger.info("First response: {}",first);
        Assertions.assertTrue(first.success());
        rateLimiter.setMaxRequestsPerMinute(10);
        IntStream.rangeClosed(1,10).forEach(i->{
            RateLimitResponse response = rateLimiter.request(1);
            logger.info("response: {} {}",i,response);
            Assertions.assertTrue(response.success());
        });
        RateLimitResponse last = rateLimiter.request(1);
        Assertions.assertFalse(last.success());
    }

    // Rate limiter is now limited to 1 minute span, so actually unit testing in 'live' time is unpractical.
    // Either we'd need to implement more granular time control for the bucket system, or add the ability to
    // move time forward for the buckets. Out of scope for this exercise.
    @Test
    public void testActualRateLimiting() {

    }
}
