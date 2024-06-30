package hu.davidder.translation.api.core.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.translation.entity.Translation;

@Configuration
@PropertySource("classpath:cache.properties")
public class CacheConfig {

	@Value("${translations.cache.duration}")
	private long translationsCacheDuration;

	@Value("${translation.cache.duration}")
	private long translationCacheDuration;
	
	@Value("${image.cache.duration}")
	private long imageCacheDuration;
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	@Bean
	public RedisCacheManagerBuilderCustomizer customizer(RedisConnectionFactory redisConnectionFactory) {
		redisConnectionFactory.getConnection().commands().flushDb(); // Purge cache on startup
		ObjectMapper mapper = new ObjectMapper();
		JavaType transactionsType = mapper.getTypeFactory().constructType(new TypeReference<Iterable<Translation>>(){});
		return builder -> builder
				.withCacheConfiguration("translations",
						RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofMillis(translationsCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(transactionsType))))
				.withCacheConfiguration("translation",
						RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofMillis(translationCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Translation.class))))
				.withCacheConfiguration("image",
						RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofMillis(imageCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Image.class))));
	}
}
