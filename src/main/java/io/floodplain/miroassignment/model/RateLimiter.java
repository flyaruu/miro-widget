package io.floodplain.miroassignment.model;

public interface RateLimiter {
    /**
     * Request a token @see RateLimitResponse for details
     *
     * @return the response, either successful or unsuccessful
     */
    RateLimitResponse request(long numberOfTokens);

    /**
     * @param refillRate The number of available tokens per minute, as well as the initial number.
     *                   (Possible 'leak' if the client is able to change the mrpm thus resetting the available count)
     */
    void setMaxRequestsPerMinute(long refillRate);
}