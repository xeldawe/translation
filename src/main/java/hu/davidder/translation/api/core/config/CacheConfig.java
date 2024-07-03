package hu.davidder.translation.api.core.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.translation.entity.Translation;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.serialization.Mapper;
import io.github.bucket4j.redis.redisson.Bucket4jRedisson;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;

@Configuration
@PropertySource("classpath:cache.properties")
public class CacheConfig {

	@Value("${translations.cache.duration}")
	private long translationsCacheDuration;

	@Value("${translation.cache.duration}")
	private long translationCacheDuration;
	
	@Value("${image.cache.duration}")
	private long imageCacheDuration;
	
	@Value("${bucket.cache.duration}")
	private long bucketCacheDuration;
	
	
	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("redisTemplate")
	@Lazy
	private RedisTemplate redistTemplate;

	@Bean
	@Lazy
	public RedisCacheManagerBuilderCustomizer customizer() {
		redistTemplate.getRequiredConnectionFactory().getConnection().commands().flushDb(); // Purge cache on startup
		ObjectMapper mapper = new ObjectMapper();
		JavaType transactionsType = mapper.getTypeFactory().constructType(new TypeReference<Iterable<Translation>>(){});
		return builder -> builder
				.withCacheConfiguration("translations", getConfiguration(translationsCacheDuration, transactionsType))
				.withCacheConfiguration("translations", getConfiguration(translationsCacheDuration, Translation.class))
				.withCacheConfiguration("image", getConfiguration(translationsCacheDuration, Image.class))
				.withCacheConfiguration("buckets", getConfiguration(translationsCacheDuration, Bucket.class))
		;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RedisCacheConfiguration getConfiguration(final long duration, final Object type) {
		RedisCacheConfiguration config =  RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(duration));
		Jackson2JsonRedisSerializer serializer = null;
		if(type instanceof JavaType) {
			serializer = new Jackson2JsonRedisSerializer((JavaType) type);
		}else {
			serializer = new Jackson2JsonRedisSerializer((Class<?>) type);
		}
		return config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
	}
	
	/**
	 * Bucket4J Reddison manager
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Bean
	@Lazy
	public RedissonBasedProxyManager proxyManager() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://localhost:6379");
		RedissonClient redissonClient = Redisson.create(config);
		CommandAsyncExecutor commandExecutor = ((Redisson)redissonClient).getCommandExecutor();
		return 	Bucket4jRedisson.casBasedBuilder(commandExecutor).expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)))
			    .keyMapper(Mapper.STRING)
			    .build();
	}
}
