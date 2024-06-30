package hu.davidder.translation.api.ratelimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Service
@PropertySource("classpath:ratelimit.properties")
public class RateLimitService {
	
	@Value("${ratelimit.global}")
	private long globalRateLimitPerSecond;
	
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(ApiKey apiKey) {
        return cache.computeIfAbsent(apiKey.getCacheKey(), k -> newBucket(apiKey));
    }

    private Bucket newBucket(ApiKey apiKey) {
        return bucket(apiKey.getPlan().getLimit());
    }
    
    private Bandwidth globalRateLimit() {
    	return Bandwidth
				.builder()
				.capacity(globalRateLimitPerSecond)
				.refillIntervally(globalRateLimitPerSecond, Duration.ofSeconds(1))
				.build();
    }
    
    
    private Bucket bucket(Bandwidth limit) {
        return Bucket
        		.builder()
        		.addLimit(globalRateLimit())
        		.addLimit(limit)
        		.build();
    }
    
}