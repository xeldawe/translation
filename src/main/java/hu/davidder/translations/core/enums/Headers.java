package hu.davidder.translations.core.enums;

import java.util.List;
import java.util.stream.Stream;

/**
 * The Headers enum represents various HTTP headers used in the application.
 * Each enum constant has a corresponding string name.
 */
public enum Headers {

    CONTENT_TYPE("Content-Type"), 
    X_RATE_LIMIT_GLOBAL_RETRY_AFTER_SECONDS("X-Rate-Limit-Global-Retry-After-Seconds"),
    X_RATE_GLOBAL_LIMIT_REMAINING("X-Rate-Global-Limit-Remaining"),
    X_RATE_LIMIT_RETRY_AFTER_SECONDS("X-Rate-Limit-Retry-After-Seconds"),
    X_RATE_LIMIT_REMAINING("X-Rate-Limit-Remaining"), 
    X_API_KEY("X-Api-Key"), 
    X_TIER("X-Tier"), 
    X_MARKET("X-Market");

    private final String name;

    /**
     * Constructor for the Headers enum.
     * 
     * @param name The string name of the header.
     */
    Headers(String name) {
        this.name = name;
    }

    /**
     * Returns the string name of the header.
     * 
     * @return The name of the header.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all header names as strings.
     * 
     * @return A list of header names.
     */
    public static List<String> asList() {
        return Stream.of(Headers.values()).map(Headers::toString).toList();
    }

    @Override
    public String toString() {
        return name;
    }
}
