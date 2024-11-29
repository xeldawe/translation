package hu.davidder.translations.core.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hu.davidder.translations.core.interceptors.MarketInterceptor;
import hu.davidder.translations.util.logger.interceptor.LoggerInterceptor;

/**
 * The MvcConfig class configures the interceptors for the application,
 * including the MarketInterceptor and LoggerInterceptor.
 */
@Configuration
@PropertySource("classpath:ratelimit.properties")
public class MvcConfig implements WebMvcConfigurer {

    @Value("${ratelimit.path.patterns:#{new String[0]}}")
    private String[] pathPatterns;

    @Autowired
    @Lazy
    private LoggerInterceptor loggerInterceptor;

    @Autowired
    @Lazy
    private MarketInterceptor marketInterceptor;

    /**
     * Adds the interceptors to the registry.
     * 
     * @param registry The InterceptorRegistry to add the interceptors to.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(marketInterceptor).addPathPatterns(Arrays.asList(pathPatterns));
        registry.addInterceptor(loggerInterceptor).addPathPatterns(Arrays.asList(pathPatterns));
    }
}
