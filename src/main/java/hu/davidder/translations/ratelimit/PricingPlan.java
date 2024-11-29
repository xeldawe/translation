package hu.davidder.translations.ratelimit;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;

/**
 * The PricingPlan enum represents different pricing plans for rate limiting.
 * Each plan has a specified bucket capacity which determines the rate limit.
 */
public enum PricingPlan {

    TRIAL(1250),      // 1250 requests per hour
    TIER1(250000),    // 250,000 requests per hour
    TIER2(1000000);   // 1,000,000 requests per hour

    private final int bucketCapacity;

    /**
     * Constructor for the PricingPlan enum.
     * Initializes the enum with the specified bucket capacity.
     * 
     * @param bucketCapacity The capacity of the bucket.
     */
    PricingPlan(int bucketCapacity) {
        this.bucketCapacity = bucketCapacity;
    }

    /**
     * Returns the Bandwidth limit for the pricing plan.
     * 
     * @return The Bandwidth limit configured for the plan.
     */
    Bandwidth getLimit() {
        return Bandwidth.builder()
                .capacity(bucketCapacity)
                .refillIntervally(bucketCapacity, Duration.ofHours(1))
                .build();
    }

    /**
     * Returns the bucket capacity of the pricing plan.
     * 
     * @return The bucket capacity.
     */
    public int bucketCapacity() {
        return bucketCapacity;
    }
}
