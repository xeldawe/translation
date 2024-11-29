package hu.davidder.translations.core.jobs;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ClearDeletedTranslations {

    @Async("JOBS")
    @Scheduled(cron="0 0 0 * * ?")  // TODO Config - at 12:00 AM every day 
    public void physicalDelete() {

    }
}
