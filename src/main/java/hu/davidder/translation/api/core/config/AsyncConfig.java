package hu.davidder.translation.api.core.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Configuration
@EnableAsync
public class AsyncConfig {

	  @Bean
	  public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(20);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("XelLookup-");
	    executor.initialize();
	    return executor;
	  }
}
