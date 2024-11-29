package hu.davidder.translations.core.config;

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
	  @Primary
	  public Executor asyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(10);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(100);
	    executor.setThreadNamePrefix("Async Thread -");
	    executor.initialize();
	    return executor;
	  }
	  
	  @Bean("SSE")
	  public Executor sseExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(10);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(20);
	    executor.setThreadNamePrefix("SSE Thread -");
	    executor.initialize();
	    return executor;
	  }
	  
	  @Bean("JOBS")
	  public Executor jobsExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(1);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(40);
	    executor.setThreadNamePrefix("JOBS Thread -");
	    executor.initialize();
	    return executor;
	  }
	  
	  
}
