package hu.davidder.translation.api.core.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	  @Bean
	  public Executor asyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(20);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("SSE & Async Thread -");
	    executor.initialize();
	    return executor;
	  }
	  
}
