package hu.davidder.translation.api.core.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hu.davidder.translation.api.ratelimit.interceptor.RateLimitInterceptor;

@Configuration
@PropertySource("classpath:ratelimit.properties")
public class MvcConfig implements WebMvcConfigurer {

	@Value("${ratelimit.path.patterns:#{new String[0]}}")
	private String[] pathPatterns;
	
	@Autowired
	@Lazy
	private RateLimitInterceptor rateLimitInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration interceptor = registry.addInterceptor(rateLimitInterceptor);
		interceptor.addPathPatterns(Arrays.asList(pathPatterns));
	}

}
