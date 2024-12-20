package hu.davidder.translations.core.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translations.core.keygenerators.ImageKeyGenerator;
import hu.davidder.translations.core.keygenerators.MarketKeyGenerator;
import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.translation.entity.Translation;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.serialization.Mapper;
import io.github.bucket4j.redis.redisson.Bucket4jRedisson;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;

/**
 * The CacheConfig class configures caching settings for the application,
 * including Redis cache management and Bucket4j rate limiting.
 */
@Configuration
@PropertySource("classpath:cache.properties")
@EnableCaching
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
    private RedisTemplate redisTemplate;

    /**
     * Customizes the RedisCacheManager with specific cache configurations.
     * 
     * @return The RedisCacheManagerBuilderCustomizer bean.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public RedisCacheManagerBuilderCustomizer customizer() {
        redisTemplate.getRequiredConnectionFactory().getConnection().commands().flushDb(); // Purge cache on startup
        ObjectMapper mapper = new ObjectMapper();
        JavaType transactionsType = mapper.getTypeFactory().constructType(new TypeReference<Iterable<Translation>>() {});
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
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Image.class))))
                .withCacheConfiguration("buckets",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(bucketCacheDuration))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Bucket.class))));
    }

    /**
     * Configures the Bucket4j Redisson manager.
     * 
     * @return The RedissonBasedProxyManager bean.
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public RedissonBasedProxyManager proxyManager() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        RedissonClient redissonClient = Redisson.create(config);
        CommandAsyncExecutor commandExecutor = ((Redisson) redissonClient).getCommandExecutor();
        return Bucket4jRedisson.casBasedBuilder(commandExecutor)
                .expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)))
                .keyMapper(Mapper.STRING)
                .build();
    }

    /**
     * Provides a MarketKeyGenerator bean.
     * 
     * @return The MarketKeyGenerator instance.
     */
    @Bean("marketKeyGenerator")
    public KeyGenerator marketKeyGenerator() {
        return new MarketKeyGenerator();
    }

    /**
     * Provides an ImageKeyGenerator bean.
     * 
     * @return The ImageKeyGenerator instance.
     */
    @Bean("imageKeyGenerator")
    public KeyGenerator imageKeyGenerator() {
        return new ImageKeyGenerator();
    }
}
