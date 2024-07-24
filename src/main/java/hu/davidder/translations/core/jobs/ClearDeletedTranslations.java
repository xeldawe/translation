package hu.davidder.translations.core.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import hu.davidder.translations.translation.service.TranslationService;

@Configuration
@EnableScheduling
public class ClearDeletedTranslations {
	
	@Lazy
	@Autowired
	private TranslationService translationService;

    @Async("JOBS")
    @Scheduled(cron="0 0 0 * * ?")  // TODO Config - at 12:00 AM every day 
    public void physicalDelete() {
    	;
    	//TODO clear non physical deleted translations
    }
}
