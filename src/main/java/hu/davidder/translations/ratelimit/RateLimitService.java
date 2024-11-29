package hu.davidder.translations.ratelimit;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

/**
 * The RateLimitService class provides methods for managing rate limits.
 * It integrates with Bucket4j to enforce rate limiting based on various strategies.
 */
@Service
@PropertySource("classpath:ratelimit.properties")
public class RateLimitService {

    @Value("${ratelimit.global}")
    private long globalRateLimitPerSecond;

    @Lazy
    @Autowired
    private ProxyManager<String> proxyManager;

    /**
     * Creates and returns the global rate limit bandwidth configuration.
     * 
     * @return The Bandwidth configuration for the global rate limit.
     */
    public Bandwidth globalRateLimit() {
        return Bandwidth.builder()
                .capacity(globalRateLimitPerSecond)
                .refillIntervally(globalRateLimitPerSecond, Duration.ofSeconds(1))
                .build();
    }

    /**
     * Retrieves a Bucket for the specified rate limit.
     * 
     * @param rateLimit The RateLimit object containing rate limit information.
     * @return The Bucket used for rate limiting.
     */
    public Bucket getBucket(final RateLimit rateLimit) {
        return proxyManager.builder()
                .build(rateLimit.getCacheKey(), () -> createBucketConfiguration(rateLimit));
    }

    /**
     * Resets the rate limit for the specified RateLimit object.
     * 
     * @param rateLimit The RateLimit object to reset.
     */
    @Async
    public void reset(final RateLimit rateLimit) {
        proxyManager.removeProxy(rateLimit.getCacheKey());
    }

    /**
     * Creates a BucketConfiguration for the specified rate limit.
     * 
     * @param rateLimit The RateLimit object containing rate limit information.
     * @return The BucketConfiguration for the rate limit.
     */
    private BucketConfiguration createBucketConfiguration(final RateLimit rateLimit) {
        Bandwidth limit = rateLimit.getRateLimitType() == RateLimitType.GLOBAL 
                ? globalRateLimit() 
                : rateLimit.getPlan().getLimit();
        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}
