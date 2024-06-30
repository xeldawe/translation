package hu.davidder.translation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = { // DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class
									// //Disable db autoconfiguration
})
@EnableCaching
public class TranslationApplication {
	public static void main(String[] args) {
		SpringApplication.run(TranslationApplication.class, args);

	}
}
