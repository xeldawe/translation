package hu.davidder.translations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { // DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class
									// //Disable db autoconfiguration
}
,scanBasePackages = {"hu.davidder.translations"}
)
public class TranslationApplication {
	public static void main(String[] args) {
		SpringApplication.run(TranslationApplication.class, args);
	}
}
