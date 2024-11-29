package hu.davidder.translations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The TranslationApplication class is the entry point for the Spring Boot application.
 * It starts the application by running the main method.
 */
@SpringBootApplication(exclude = {}, scanBasePackages = { "hu.davidder.translations" })
public class TranslationApplication {

    /**
     * The main method that serves as the entry point for the Spring Boot application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(TranslationApplication.class, args);
    }
}
