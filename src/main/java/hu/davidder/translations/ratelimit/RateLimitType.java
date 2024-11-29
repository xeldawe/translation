package hu.davidder.translations.ratelimit;

/**
 * The RateLimitType enum represents different types of rate limits.
 * It includes USER, API_KEY, and GLOBAL rate limit types.
 */
public enum RateLimitType {

    /** Represents a rate limit applied to individual users. */
    USER,

    /** Represents a rate limit applied to API keys. */
    API_KEY,

    /** Represents a global rate limit applied across the entire system. */
    GLOBAL
}
