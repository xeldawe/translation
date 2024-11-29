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

@Service
@PropertySource("classpath:ratelimit.properties")
public class RateLimitService {
	
	@Value("${ratelimit.global}")
	private long globalRateLimitPerSecond;

	@Lazy
	@SuppressWarnings("rawtypes")
	@Autowired
	private ProxyManager proxyManager;
	
    public Bandwidth globalRateLimit() {
    	return Bandwidth
				.builder()
				.capacity(globalRateLimitPerSecond)
				.refillIntervally(globalRateLimitPerSecond, Duration.ofSeconds(1))
				.build();
    }
    
	@SuppressWarnings("unchecked")
	public Bucket getBucket(final RateLimit rateLimit) {
		return proxyManager
				.builder()
				.build(rateLimit.getCacheKey(), () -> createBucketConfiguration(rateLimit));
	}

	@SuppressWarnings("unchecked")
	@Async
	public void reset(final RateLimit rateLimit) {
		proxyManager.removeProxy(rateLimit.getCacheKey());
	}

	private BucketConfiguration createBucketConfiguration(final RateLimit rateLimit) {
		return BucketConfiguration.builder()
				.addLimit(rateLimit.getRateLimitType().equals(RateLimitType.GLOBAL)?globalRateLimit():rateLimit.getPlan().getLimit())
				.build();
	}

    
}