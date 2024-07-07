package hu.davidder.translations.core.config;



import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hu.davidder.translations.util.logger.interceptor.LoggerInterceptor;

@Configuration
@PropertySource("classpath:ratelimit.properties")
public class MvcConfig implements WebMvcConfigurer {

	@Value("${ratelimit.path.patterns:#{new String[0]}}")
	private String[] pathPatterns;
	
	@Autowired
	@Lazy
	private LoggerInterceptor loggerInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration interceptor = registry.addInterceptor(loggerInterceptor);
		interceptor.addPathPatterns(Arrays.asList(pathPatterns));
	}

}
