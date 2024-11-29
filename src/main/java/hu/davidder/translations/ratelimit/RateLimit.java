package hu.davidder.translations.ratelimit;

import java.io.Serializable;

import io.github.bucket4j.Bucket;

/**
 * The RateLimit class represents rate limiting information for various clients.
 * It includes details such as key, IP address, pricing plan, bucket for rate limiting, and the type of rate limit.
 */
public class RateLimit implements Serializable {

    private static final long serialVersionUID = 8666081824450498131L;

    private String key;
    private String ipAddress;
    private PricingPlan plan;
    private Bucket bucket;
    private RateLimitType type;

    /**
     * Default constructor.
     * Creates a default instance of RateLimit.
     */
    public RateLimit() {}

    /**
     * Parameterized constructor.
     * Creates an instance of RateLimit with the specified parameters.
     * 
     * @param key The key associated with the rate limit.
     * @param plan The pricing plan for the rate limit.
     * @param ipAddress The IP address associated with the rate limit.
     * @param type The type of rate limit.
     */
    public RateLimit(String key, PricingPlan plan, String ipAddress, RateLimitType type) {
        this.key = key;
        this.plan = plan;
        this.ipAddress = ipAddress;
        this.type = type;
    }

    /**
     * Returns the key associated with the rate limit.
     * 
     * @return The key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key associated with the rate limit.
     * 
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the pricing plan for the rate limit.
     * 
     * @return The pricing plan.
     */
    public PricingPlan getPlan() {
        return plan;
    }

    /**
     * Sets the pricing plan for the rate limit.
     * 
     * @param plan The pricing plan to set.
     */
    public void setPlan(PricingPlan plan) {
        this.plan = plan;
    }

    /**
     * Returns the IP address associated with the rate limit.
     * 
     * @return The IP address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address associated with the rate limit.
     * 
     * @param ipAddress The IP address to set.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Returns the cache key used for rate limiting.
     * 
     * @return The cache key.
     */
    public String getCacheKey() {
        return key + "-" + ipAddress + "-" + type;
    }

    /**
     * Returns the bucket used for rate limiting.
     * 
     * @return The bucket.
     */
    public Bucket getBucket() {
        return bucket;
    }

    /**
     * Sets the bucket used for rate limiting.
     * 
     * @param bucket The bucket to set.
     */
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Returns the type of rate limit.
     * 
     * @return The rate limit type.
     */
    public RateLimitType getRateLimitType() {
        return type;
    }

    /**
     * Sets the type of rate limit.
     * 
     * @param type The rate limit type to set.
     */
    public void setRateLimitType(RateLimitType type) {
        this.type = type;
    }
}
