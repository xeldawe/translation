package hu.davidder.translation.api.ratelimit.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import hu.davidder.translation.api.ratelimit.ApiKey;
import hu.davidder.translation.api.ratelimit.PricingPlan;
import hu.davidder.translation.api.ratelimit.RateLimitService;
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
	private static final String TIER = "X-Tier";
	
	@Autowired
	private RateLimitService pricingPlanService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String apiKey = request.getHeader(HEADER_API_KEY);
		if (apiKey == null || apiKey.isEmpty()) {
			String urlApiKey = null;
			try { //Try to get api key (URL)
				Map parameterMap = request.getParameterMap();
				String[] apiKeys = (String[]) parameterMap.get("apiKey");
				urlApiKey = apiKeys[0];
			} catch (Exception e) {
				// Api key not found in the URL
			}
			if (urlApiKey != null) {
				apiKey = urlApiKey;
			} 
		}

		ApiKey key = getApiKey(apiKey, request.getRemoteAddr());
		Bucket tokenBucket = pricingPlanService.resolveBucket(key);
		ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
		response.addHeader(TIER, key.getPlan().toString());
		if (probe.isConsumed()) {
			response.addHeader(HEADER_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
			return true;
		} else {
			long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.addHeader(HEADER_RETRY_AFTER, String.valueOf(waitForRefill));
			if(waitForRefill == 0) {
				response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Stop spamming"); // 429
			}else {
			response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota"); // 429
			}
			return false;
		}
	}
	
	
	private ApiKey getApiKey(String apiKey, String ipAddress) {
		
		ApiKey key = null;
		if (apiKey != null && !apiKey.isEmpty()) {
			//DUMMY START
			key = getDummyApiKey(apiKey, ipAddress);
			//DUMMY END
			//TODO DB
		}
		if(key == null) {
			key = new ApiKey(ipAddress, PricingPlan.FREE, ipAddress);
		}
		return key;
	}
	
	private ApiKey getDummyApiKey(String apiKey, String ipAddress) {
		ApiKey key = null;
			if (apiKey.startsWith("PX001-")) { // TODO
				return new ApiKey(apiKey, PricingPlan.PROFESSIONAL, ipAddress);
			} else if (apiKey.startsWith("BX001-")) { // TODO
				return new ApiKey(apiKey, PricingPlan.BASIC, ipAddress);
			} else {
				return new ApiKey(ipAddress, PricingPlan.FREE, ipAddress);
			}
	}
}