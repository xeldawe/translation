package hu.davidder.translations.core.jobs;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The ClearDeletedTranslations class is a scheduled job configuration
 * that runs a task to physically delete translations marked for deletion.
 */
@Configuration
@EnableScheduling
public class ClearDeletedTranslations {

    /**
     * Scheduled method to physically delete translations marked for deletion.
     * This method runs at 12:00 AM every day.
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Runs at 12:00 AM every day
    public void physicalDelete() {
        // TODO: Implement the physical deletion logic for deleted translations
    }
}
