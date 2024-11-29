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


@Component
@PropertySource("classpath:ratelimit.properties")
public class RateLimitFilter extends  OncePerRequestFilter  {


	@Lazy
	@Autowired
	private RateLimitService rateLimitService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String RateLimit = request.getHeader(Headers.X_API_KEY.name());
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
		filterChain.doFilter(request, response);
	}

	private boolean checkKey(final RateLimit rateLimit, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
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

	//TODO
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

	@Deprecated
	private RateLimit getDummyRateLimit(final String rateLimit, final String ipAddress, final RateLimitType type) {
		//RateLimit key = null;
		if (rateLimit.startsWith("123456")) { // TODO
			return new RateLimit(rateLimit, PricingPlan.TIER2, ipAddress, type);
		} else if (rateLimit.startsWith("zzzzzzz")) { // TODO
			return new RateLimit(rateLimit, PricingPlan.TIER1, ipAddress, type);
		} else {
			return new RateLimit(ipAddress, PricingPlan.TRIAL, ipAddress, type);
		}
	}


}
