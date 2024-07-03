package hu.davidder.translation.api.ratelimit.interceptor;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import hu.davidder.translation.api.ratelimit.RateLimit;
import hu.davidder.translation.api.ratelimit.PricingPlan;
import hu.davidder.translation.api.ratelimit.RateLimitService;
import hu.davidder.translation.api.ratelimit.RateLimitType;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:ratelimit.properties")
public class RateLimitInterceptor implements HandlerInterceptor {

	private static final String HEADER_API_KEY = "X-Api-Key";
	private static final String HEADER_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
	private static final String HEADER_RETRY_AFTER = "X-Rate-Limit-Retry-After-Seconds";
	private static final String HEADER_GLOBAL_LIMIT_REMAINING = "X-Rate-Global-Limit-Remaining";
	private static final String HEADER_GLOBAL_RETRY_AFTER = "X-Rate-Global-Limit-Retry-After-Seconds";
	private static final String TIER = "X-Tier";

	@Autowired
	private RateLimitService rateLimitService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String RateLimit = request.getHeader(HEADER_API_KEY);
		if (RateLimit == null || RateLimit.isEmpty()) {
			String urlRateLimit = null;
			try { // Try to get api key (URL)
				Map parameterMap = request.getParameterMap();
				String[] RateLimits = (String[]) parameterMap.get("RateLimit");
				urlRateLimit = RateLimits[0];
			} catch (Exception e) {
				// Api key not found in the URL
			}
			if (urlRateLimit != null) {
				RateLimit = urlRateLimit;
			}
		}
		RateLimit global = getRateLimit(RateLimit, request.getRemoteAddr(), RateLimitType.GLOBAL);
		RateLimit key = getRateLimit(RateLimit, request.getRemoteAddr(), RateLimitType.API_KEY);
		boolean result = true;
		result = checkKey(global, request, response);
		if (result) {
			checkKey(key, request, response);
		}
		return result;
	}

	private boolean checkKey(final RateLimit rateLimit, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		boolean isGlobal = rateLimit.getRateLimitType().equals(RateLimitType.GLOBAL);
		Bucket tokenBucket = rateLimitService.getBucket(rateLimit);
		ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
		if (!isGlobal) {
			response.addHeader(TIER, rateLimit.getPlan().toString());
		}
		if (probe.isConsumed()) {
			response.addHeader(isGlobal ? HEADER_GLOBAL_LIMIT_REMAINING : HEADER_LIMIT_REMAINING,
					String.valueOf(probe.getRemainingTokens()));
			return true;
		} else {
			long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.addHeader(isGlobal ? HEADER_GLOBAL_RETRY_AFTER : HEADER_RETRY_AFTER,
					String.valueOf(waitForRefill));
			response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
					isGlobal ? "Stop spamming" : "You have exhausted your API Request Quota"); // 429
			return false;
		}
	}

	private RateLimit getRateLimit(final String rateLimit, final String ipAddress, final RateLimitType type) {

		RateLimit key = null;
		if (rateLimit != null && !rateLimit.isEmpty()) {
			// DUMMY START
			key = getDummyRateLimit(rateLimit, ipAddress, type);
			// DUMMY END
			// TODO DB
		}
		if (key == null) {
			key = new RateLimit(ipAddress, PricingPlan.TRIAL, ipAddress, type);
		}
		return key;
	}

	private RateLimit getDummyRateLimit(final String rateLimit, final String ipAddress, final RateLimitType type) {
		RateLimit key = null;
		if (rateLimit.startsWith("123456")) { // TODO
			return new RateLimit(rateLimit, PricingPlan.TIER2, ipAddress, type);
		} else if (rateLimit.startsWith("zzzzzzz")) { // TODO
			return new RateLimit(rateLimit, PricingPlan.TIER1, ipAddress, type);
		} else {
			return new RateLimit(ipAddress, PricingPlan.TRIAL, ipAddress, type);
		}
	}
}