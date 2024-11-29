package hu.davidder.translations.ratelimit.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import hu.davidder.translations.core.enums.Headers;
import hu.davidder.translations.ratelimit.PricingPlan;
import hu.davidder.translations.ratelimit.RateLimit;
import hu.davidder.translations.ratelimit.RateLimitService;
import hu.davidder.translations.ratelimit.RateLimitType;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The RateLimitFilter class filters incoming requests to enforce rate limits.
 * It uses Bucket4j for rate limiting and integrates with the RateLimitService.
 */
@Component
@PropertySource("classpath:ratelimit.properties")
public class RateLimitFilter extends OncePerRequestFilter {

    @Lazy
    @Autowired
    private RateLimitService rateLimitService;

    /**
     * Filters each request to check rate limits before proceeding.
     * 
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rateLimit = resolveRateLimit(request);

        RateLimit global = getRateLimit(rateLimit, request.getRemoteAddr(), RateLimitType.GLOBAL);
        RateLimit key = getRateLimit(rateLimit, request.getRemoteAddr(), RateLimitType.API_KEY);

        if (checkKey(global, request, response) && checkKey(key, request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Checks the rate limit for the given RateLimit object.
     * 
     * @param rateLimit The RateLimit object.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @return true if the request is within the rate limit, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private boolean checkKey(RateLimit rateLimit, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isGlobal = rateLimit.getRateLimitType().equals(RateLimitType.GLOBAL);
        Bucket tokenBucket = rateLimitService.getBucket(rateLimit);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (!isGlobal) {
            response.addHeader(Headers.X_TIER.name(), rateLimit.getPlan().toString());
        }

        if (probe.isConsumed()) {
            response.addHeader(isGlobal ? Headers.X_RATE_GLOBAL_LIMIT_REMAINING.name() : Headers.X_RATE_LIMIT_REMAINING.name(),
                    String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader(isGlobal ? Headers.X_RATE_LIMIT_GLOBAL_RETRY_AFTER_SECONDS.name() : Headers.X_RATE_LIMIT_RETRY_AFTER_SECONDS.name(),
                    String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                    isGlobal ? "Stop spamming" : "You have exhausted your API Request Quota"); // 429
            return false;
        }
    }

    /**
     * Resolves the rate limit key from the request headers or parameters.
     * 
     * @param request The HTTP request.
     * @return The rate limit key.
     */
    private String resolveRateLimit(HttpServletRequest request) {
        String rateLimit = request.getHeader(Headers.X_API_KEY.name());
        if (rateLimit == null || rateLimit.isEmpty()) {
            try {
                Map<String, String[]> parameterMap = request.getParameterMap();
                String[] rateLimits = parameterMap.get("RateLimit");
                if (rateLimits != null && rateLimits.length > 0) {
                    rateLimit = rateLimits[0];
                }
            } catch (Exception e) {
                // Log or handle the exception if necessary
            }
        }
        return rateLimit;
    }

    /**
     * Retrieves the appropriate RateLimit object based on the rate limit key and IP address.
     * 
     * @param rateLimit The rate limit key.
     * @param ipAddress The IP address.
     * @param type The type of rate limit.
     * @return The RateLimit object.
     */
    private RateLimit getRateLimit(String rateLimit, String ipAddress, RateLimitType type) {
        if (rateLimit != null && !rateLimit.isEmpty()) {
            return getDummyRateLimit(rateLimit, ipAddress, type); // Replace this with actual DB retrieval logic
        }
        return new RateLimit(ipAddress, PricingPlan.TRIAL, ipAddress, type);
    }

    /**
     * Returns a dummy RateLimit object for testing purposes.
     * 
     * @param rateLimit The rate limit key.
     * @param ipAddress The IP address.
     * @param type The type of rate limit.
     * @return The dummy RateLimit object.
     */
    @Deprecated
    private RateLimit getDummyRateLimit(String rateLimit, String ipAddress, RateLimitType type) {
        if (rateLimit.startsWith("123456")) {
            return new RateLimit(rateLimit, PricingPlan.TIER2, ipAddress, type);
        } else if (rateLimit.startsWith("zzzzzzz")) {
            return new RateLimit(rateLimit, PricingPlan.TIER1, ipAddress, type);
        } else {
            return new RateLimit(ipAddress, PricingPlan.TRIAL, ipAddress, type);
        }
    }
}
